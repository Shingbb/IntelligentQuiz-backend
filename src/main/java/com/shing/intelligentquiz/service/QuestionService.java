package com.shing.intelligentquiz.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shing.intelligentquiz.model.dto.question.AiGenerateQuestionRequest;
import com.shing.intelligentquiz.model.dto.question.QuestionContentDTO;
import com.shing.intelligentquiz.model.dto.question.QuestionQueryRequest;
import com.shing.intelligentquiz.model.entity.Question;
import com.shing.intelligentquiz.model.vo.QuestionVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目服务
 *

 */
public interface QuestionService extends IService<Question> {

    /**
     * 校验数据
     *
     * @param question 待校验的数据
     * @param add 对创建的数据进行校验
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest  查询条件
     * @return  QueryWrapper<Question>
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);
    
    /**
     * 获取题目封装
     *
     * @param question 题目
     * @param request 请求
     * @return QuestionVO
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage 分页
     * @param request 请求
     * @return Page<QuestionVO>
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * AI 生成题目
     * @param aiGenerateQuestionRequest AI生成题目请求
     * @return  List<QuestionContentDTO>
     */
    List<QuestionContentDTO> generateQuestion(AiGenerateQuestionRequest aiGenerateQuestionRequest);

    /**
     * AI 流式生成题目
     *
     * @param aiGenerateQuestionRequest AI生成题目请求
     * @return SseEmitter
     */
    SseEmitter generateQuestionsByFlowable(AiGenerateQuestionRequest aiGenerateQuestionRequest);
}
