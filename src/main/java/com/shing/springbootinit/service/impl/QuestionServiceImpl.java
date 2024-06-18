package com.shing.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shing.springbootinit.common.ErrorCode;
import com.shing.springbootinit.constant.CommonConstant;
import com.shing.springbootinit.exception.ThrowUtils;
import com.shing.springbootinit.mapper.QuestionMapper;
import com.shing.springbootinit.model.dto.question.QuestionQueryRequest;
import com.shing.springbootinit.model.entity.App;
import com.shing.springbootinit.model.entity.Question;

import com.shing.springbootinit.model.entity.User;
import com.shing.springbootinit.model.vo.QuestionVO;
import com.shing.springbootinit.model.vo.UserVO;
import com.shing.springbootinit.service.AppService;
import com.shing.springbootinit.service.QuestionService;
import com.shing.springbootinit.service.UserService;
import com.shing.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题目服务实现类
 */
@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

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
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 可选操作：补充题目VO对象的相关信息
        return questionVO;
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
        List<QuestionVO> questionVOList = questionList.stream().map(question -> QuestionVO.objToVo(question)).collect(Collectors.toList());
        // 可选操作：补充题目VO对象的相关信息

        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

}
