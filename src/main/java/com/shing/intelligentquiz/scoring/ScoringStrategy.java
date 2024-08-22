package com.shing.intelligentquiz.scoring;

import com.shing.intelligentquiz.model.entity.App;
import com.shing.intelligentquiz.model.entity.UserAnswer;

import java.util.List;

/**
 * 评分策略
 *
 * @author shing
 */
public interface ScoringStrategy {

    /**
     * 执行评分
     * @param choices 用户选择的选项
     * @param app 应用信息
     * @return 评分结果
     * @throws Exception 异常
     */
    UserAnswer doScore(List<String> choices, App app) throws Exception;
}
