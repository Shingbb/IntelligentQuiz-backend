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
     * @param choices
     * @param app
     * @return
     * @throws Exception
     */
    UserAnswer doScore(List<String> choices, App app) throws Exception;
}
