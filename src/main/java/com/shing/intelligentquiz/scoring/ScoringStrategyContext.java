package com.shing.intelligentquiz.scoring;

import com.shing.intelligentquiz.common.ErrorCode;
import com.shing.intelligentquiz.exception.BusinessException;
import com.shing.intelligentquiz.model.entity.App;
import com.shing.intelligentquiz.model.entity.UserAnswer;
import com.shing.intelligentquiz.model.enums.AppScoringStrategyEnum;
import com.shing.intelligentquiz.model.enums.AppTypeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 根据不同的应用类别和评分策略的全局执行器 -- 编程式
 *
 * @author shing
 */
@Service
@Deprecated
public class ScoringStrategyContext {

    /**
     * 自定义分数计算策略
     */
    @Resource
    private CustomScoreScoringStrategy customScoreScoringStrategy;

    /**
     * 自定义测试分数计算策略
     */
    @Resource
    private CustomTestScoringStrategy customTestScoringStrategy;

    /**
     * 根据用户的选择列表和应用信息进行评分。
     *
     * @param choiceList 用户的选择列表
     * @param app        应用信息
     * @return 计算后的用户答案对象，包含评分信息
     * @throws Exception 如果找不到匹配的评分策略则抛出异常
     */
    public UserAnswer doScore(List<String> choiceList, App app) throws Exception {
        // 根据应用类型和评分策略获取对应的枚举值
        AppTypeEnum appTypeEnum = AppTypeEnum.getEnumByValue(app.getAppType());
        AppScoringStrategyEnum appScoringStrategyEnum = AppScoringStrategyEnum.getEnumByValue(app.getScoringStrategy());

        // 检查是否有有效的枚举值，如果没有则抛出业务异常
        if (appTypeEnum == null || appScoringStrategyEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
        }

        // 根据应用类型和评分策略选择对应的处理逻辑
        // 根据不同的应用类别和评分策略，选择对应的策略执行
        switch (appTypeEnum) {
            case SCORE:
                // 分数类型应用的评分策略
                switch (appScoringStrategyEnum) {
                    case CUSTOM:
                        // 使用自定义策略计算分数
                        return customScoreScoringStrategy.doScore(choiceList, app);
                    case AI:
                        // AI策略尚未实现
                        break;
                }
                break;
            case TEST:
                // 测试类型应用的评分策略
                switch (appScoringStrategyEnum) {
                    case CUSTOM:
                        // 使用自定义策略计算分数
                        return customTestScoringStrategy.doScore(choiceList, app);
                    case AI:
                        // AI策略尚未实现
                        break;
                }
                break;
        }

        // 如果没有找到匹配的评分策略，则抛出业务异常
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
    }
}
