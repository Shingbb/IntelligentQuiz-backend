package com.shing.springbootinit.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shing.springbootinit.manager.AiManager;
import com.shing.springbootinit.model.dto.question.QuestionAnswerDTO;
import com.shing.springbootinit.model.dto.question.QuestionContentDTO;
import com.shing.springbootinit.model.entity.App;
import com.shing.springbootinit.model.entity.Question;
import com.shing.springbootinit.model.entity.UserAnswer;
import com.shing.springbootinit.model.vo.QuestionVO;
import com.shing.springbootinit.service.QuestionService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * AI 测评类应用评分策略
 *
 * @author shing
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AITestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    /**
     * AI 评分系统消息
     */
    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象";

    /**
     * 根据用户选择和应用信息进行评分。
     *
     * @param choices 用户的选择列表。
     * @param app     相关的应用信息。
     * @return 用户答案对象，包含评分结果和相关信息。
     * @throws Exception 如果评分过程中出现错误。
     */
    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        // 获取应用ID
        Long appId = app.getId();
        // 根据应用ID查询对应的应用题目
        // 1. 根据 id 查询到题目
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        // 将题目实体转换为题目VO对象
        // 获取题目内容
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 从题目VO中获取题目内容
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        // 构造向AI系统请求的用户消息
        // 2. 调用 AI 获取结果
        // 封装 Prompt
        String userMessage = getAiTestScoringUserMessage(app, questionContent, choices);
        // 向AI系统发送请求，获取评分结果
        // AI 生成
        String result = aiManager.doSyncStableRequest(AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
        // 从AI系统的响应中提取出实际的评分结果JSON字符串
        // 截取需要的 JSON 信息
        int start = result.indexOf("{");
        int end = result.lastIndexOf("}");
        String json = result.substring(start, end + 1);

        // 将评分结果JSON字符串转换为用户答案对象
        // 3. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
        // 设置用户答案的相关信息
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        // 返回用户答案对象
        return userAnswer;
    }

    /**
     * AI 评分用户消息封装
     * 该方法封装了用户针对每个问题的回答，以及应用的名称和描述，为后续的AI评分提供必要信息。
     *
     * @param app                    应用信息对象，包含应用名称和描述。
     * @param questionContentDTOList 问题内容列表，每个问题包括问题的标题。
     * @param choices                用户的选择列表，与问题列表对应，表示用户对每个问题的回答。
     * @return 返回一个字符串，包含应用信息和用户针对每个问题的回答。
     */
    private String getAiTestScoringUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        // 初始化一个字符串构建器，用于组装最终的用户消息字符串。
        StringBuilder userMessage = new StringBuilder();
        // 添加应用的名称和描述到用户消息中。
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");

        // 初始化一个问题回答列表，用于存储每个问题和用户的回答。
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        // 遍历问题内容列表，为每个问题创建一个QuestionAnswerDTO对象，并填充问题标题和用户的回答。
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        // 将问题回答列表转换为JSON字符串，并添加到用户消息中。
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));

        // 返回组装好的用户消息字符串。
        return userMessage.toString();
    }

}
