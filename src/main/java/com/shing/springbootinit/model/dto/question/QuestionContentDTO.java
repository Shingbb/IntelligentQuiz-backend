package com.shing.springbootinit.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 问题内容数据传输对象类。
 * 用于封装问题的标题及其选项。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionContentDTO {

    /**
     * 问题的标题。
     */
    private String title;

    /**
     * 问题的选项列表。
     * 每个选项包含结果、分数、值和键。
     */
    private List<Option> options;

    /**
     * 问题选项类。
     * 用于封装选项的具体内容，包括结果、分数、值和键。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option {
        /**
         * 选项的结果描述。
         */
        private String result;
        /**
         * 选项对应的分数。
         */
        private int score;
        /**
         * 选项的显示值。
         */
        private String value;
        /**
         * 选项的唯一标识键。
         */
        private String key;
    }
}
