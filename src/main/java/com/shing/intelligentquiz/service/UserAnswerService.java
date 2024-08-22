package com.shing.intelligentquiz.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shing.intelligentquiz.model.dto.useranswer.UserAnswerQueryRequest;
import com.shing.intelligentquiz.model.entity.UserAnswer;
import com.shing.intelligentquiz.model.vo.UserAnswerVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户答案服务
 *

 */
public interface UserAnswerService extends IService<UserAnswer> {

    /**
     * 校验数据
     *
     * @param userAnswer 用户答案
     * @param add 对创建的数据进行校验
     */
    void validUserAnswer(UserAnswer userAnswer, boolean add);

    /**
     * 获取查询条件
     *
     * @param userAnswerQueryRequest 用户答案查询请求
     * @return 查询条件
     */
    QueryWrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequest userAnswerQueryRequest);
    
    /**
     * 获取用户答案封装
     *
     * @param userAnswer 用户答案
     * @param request 请求
     * @return 用户答案封装
     */
    UserAnswerVO getUserAnswerVO(UserAnswer userAnswer, HttpServletRequest request);

    /**
     * 分页获取用户答案封装
     *
     * @param userAnswerPage 分页对象
     * @param request 请求
     * @return 分页用户答案封装
     */
    Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> userAnswerPage, HttpServletRequest request);
}
