package com.shing.intelligentquiz.model.dto.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI 生成题目请求
 *
 * @author shing
 */
@Data
public class AiGenerateQuestionRequest implements Serializable {

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 题目数
     */
    int questionNumber = 10;

    /**
     * 选项数
     */
    int optionNumber = 2;


    @Serial
    private static final long serialVersionUID = 1L;

}
