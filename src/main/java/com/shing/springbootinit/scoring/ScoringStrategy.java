package com.shing.springbootinit.scoring;

import com.shing.springbootinit.model.entity.App;
import com.shing.springbootinit.model.entity.UserAnswer;

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
