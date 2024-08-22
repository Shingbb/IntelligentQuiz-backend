package com.shing.intelligentquiz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shing.intelligentquiz.common.ErrorCode;
import com.shing.intelligentquiz.constant.CommonConstant;
import com.shing.intelligentquiz.exception.ThrowUtils;
import com.shing.intelligentquiz.manager.AiManager;
import com.shing.intelligentquiz.mapper.QuestionMapper;
import com.shing.intelligentquiz.model.dto.question.AiGenerateQuestionRequest;
import com.shing.intelligentquiz.model.dto.question.QuestionContentDTO;
import com.shing.intelligentquiz.model.dto.question.QuestionQueryRequest;
import com.shing.intelligentquiz.model.entity.App;
import com.shing.intelligentquiz.model.entity.Question;
import com.shing.intelligentquiz.model.enums.AppTypeEnum;
import com.shing.intelligentquiz.model.vo.QuestionVO;
import com.shing.intelligentquiz.service.AppService;
import com.shing.intelligentquiz.service.QuestionService;
import com.shing.intelligentquiz.utils.SqlUtils;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 题目服务实现类
 */
@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private AppService appService;

    @Resource
    private AiManager aiManager;

    /**
     * 验证题目信息的合法性
     *
     * @param question 题目实体
     * @param add      标识是否为新增操作
     * @throws RuntimeException 如果信息不合法则抛出异常
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String questionContent = question.getQuestionContent();
        Long appId = question.getAppId();
        // 对新增数据的额外验证
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(questionContent), ErrorCode.PARAMS_ERROR, "题目内容不能为空");
            ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId 非法");
        }
        // 对修改数据的验证
        if (appId != null) {
            App app = appService.getById(appId);
            ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");
        }
    }

    /**
     * 根据查询条件构造查询Wrapper
     *
     * @param questionQueryRequest 查询条件请求对象
     * @return 查询条件Wrapper
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        // 从请求对象中获取条件值
        Long id = questionQueryRequest.getId();
        String questionContent = questionQueryRequest.getQuestionContent();
        Long appId = questionQueryRequest.getAppId();
        Long userId = questionQueryRequest.getUserId();
        Long notId = questionQueryRequest.getNotId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        // 构造查询条件
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(questionContent), "questionContent", questionContent);
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 将题目实体转换为题目VO对象
     *
     * @param question 题目实体
     * @param request  HTTP请求对象，目前未使用
     * @return 转换后的题目VO对象
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        // 可选操作：补充题目VO对象的相关信息
        return QuestionVO.objToVo(question);
    }

    /**
     * 分页获取题目列表，并将每个题目转换为VO对象
     *
     * @param questionPage 分页对象，包含题目列表
     * @param request      HTTP请求对象，目前未使用
     * @return 转换后的分页对象，包含题目VO列表
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 题目实体列表转换为VO对象列表
        List<QuestionVO> questionVOList = questionList.stream().map(QuestionVO::objToVo).toList();

        // 可选操作：补充题目VO对象的相关信息

        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }


    // region AI 生成题目功能

    /**
     * AI 生成题目系统消息
     */
    private static final String GENERATE_QUESTION_SYSTEM_MESSAGE = """
    你是一位严谨的出题专家，我会给你如下信息：
    ```
    应用名称，\s
    【【【应用描述】】】，
    应用类别，\s
    要生成的题目数，\s
    每个题目的选项数\s
    ```
    请你根据上述信息，按照以下步骤来出题：
    1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复
    2. 严格按照下面的 json 格式输出题目和选项
    ```
    [{"options":[{"value":"选项内容","key":"A"},{"value":"","key":"B"}],"title":"题目标题"}]
    ```
    title 是题目，options 是选项，每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容
    3. 检查题目是否包含序号，若包含序号则去除序号
    4. 返回的题目列表格式必须为 JSON 数组，
    一定是json形式
   \s""";


    /**
     * 生成题目用户消息
     *
     * @param app            应用信息对象，包含应用的名称、描述和类型等。
     * @param questionNumber 需要生成的题目数量。
     * @param optionNumber   每道题目对应的选项数量。
     * @return 返回构造好的用户消息字符串。
     */
    private String getGenerateQuestionUserMessage(App app, int questionNumber, int optionNumber) {
        return app.getAppName() + "\n" +
                app.getAppDesc() + "\n" +
                Objects.requireNonNull(AppTypeEnum.getEnumByValue(app.getAppType())).getText() + "类" + "\n" +
                questionNumber + "\n" +
                optionNumber;
    }

    /**
     * AI 生成题目
     * @param aiGenerateQuestionRequest AI生成题目请求
     * @return 生成的题目
     */
    @Override
    public List<QuestionContentDTO> generateQuestion(AiGenerateQuestionRequest aiGenerateQuestionRequest) {
        // 获取请求中的应用ID、题目数量和选项数量
        // 获取参数
        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();

        // 根据应用ID获取应用信息
        App app = appService.getById(appId);
        // 校验应用信息是否存在
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);

        // 封装用户消息，用于请求AI生成题目
        // 封装 Prompt
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);
        log.info(userMessage);

        // 调用AI管理器，发送用户消息请求生成题目
        // AI 生成--默认
        String result = aiManager.doSyncRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage, null);

        // 解析AI返回的结果，提取题目数据
        // 结果处理
        int start = result.indexOf("[");
        int end = result.lastIndexOf("]");
        String json = result.substring(start, end + 1);
        // 返回生成的题目列表
        return JSONUtil.toList(json, QuestionContentDTO.class);
    }

    /**
     * AI 流式生成题目
     * @param aiGenerateQuestionRequest AI生成题目请求
     * @return 生成的题目
     */
    @Override
    public SseEmitter generateQuestionsByFlowable(AiGenerateQuestionRequest aiGenerateQuestionRequest) {
        ThrowUtils.throwIf(aiGenerateQuestionRequest == null, ErrorCode.PARAMS_ERROR);

        Long appId = aiGenerateQuestionRequest.getAppId();
        int questionNumber = aiGenerateQuestionRequest.getQuestionNumber();
        int optionNumber = aiGenerateQuestionRequest.getOptionNumber();

        // 获取应用信息
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);

        // 封装 Prompt
        String userMessage = getGenerateQuestionUserMessage(app, questionNumber, optionNumber);

        // 建立 SSE 连接对象
        SseEmitter emitter = new SseEmitter(0L);
        // AI 生成， SSE 流式返回
        Flowable<ModelData> modelDataFlowable = aiManager.doStreamRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage, null);

        StringBuilder contentBuilder = new StringBuilder();

        // 左括号计算器，除了默认值外，当回归为 0 时，表示当前累计的花括号数量为 0，可以截取
        AtomicInteger flag = new AtomicInteger(0);

        // 在IO线程上对模型数据流进行操作
        modelDataFlowable
                .observeOn(Schedulers.io())
                // 提取每个数据块中的内容
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())
                // 移除内容中的所有空格
                .map(message -> message.replaceAll("\\s", ""))
                // 过滤掉空白内容
                .filter(StrUtil::isNotBlank)
                // 将内容拆分为单个字符，并将其转换为Flowable流
                .flatMap(message -> {
                    List<Character> charList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        charList.add(c);
                    }
                    return Flowable.fromIterable(charList);
                })
                // 处理每个字符，累加花括号内的内容，并在遇到右花括号时发送累计内容
                .doOnNext(c -> {
                    if (c == '{') {
                        flag.addAndGet(1);
                    }
                    if (flag.get() > 0) {
                        contentBuilder.append(c);
                    }
                    if (c == '}') {
                        flag.addAndGet(-1);
                        if (flag.get() == 0) {
                            // 拼接题目，平且通过 SSE 返回给前端
                            emitter.send(JSONUtil.toJsonStr(contentBuilder.toString()));
                             // 重置内容构建器
                            contentBuilder.setLength(0);
                        }
                    }
                })
                // 完成时，发送完成信号
                .doOnComplete(emitter::complete)
                // 订阅并处理流中的数据
                .subscribe();

        return emitter;
    }

    // endregion

}
