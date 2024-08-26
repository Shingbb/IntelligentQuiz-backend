package com.shing.intelligentquiz;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shing.intelligentquiz.common.TestBase;
import com.shing.intelligentquiz.model.entity.UserAnswer;
import com.shing.intelligentquiz.service.UserAnswerService;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author shing
 */
class UserAnswerShardingTest extends TestBase {

    @Resource
    private UserAnswerService userAnswerService;

    @Test
    void test() {

        // 创建并保存第一个用户答案
        UserAnswer userAnswer1 = new UserAnswer();
        userAnswer1.setAppId(1L);
        userAnswer1.setUserId(1L);
        userAnswer1.setChoices("1");
        userAnswerService.save(userAnswer1);

        // 创建并保存第二个用户答案
        UserAnswer userAnswer2 = new UserAnswer();
        userAnswer2.setAppId(2L);
        userAnswer2.setUserId(1L);
        userAnswer2.setChoices("2");
        userAnswerService.save(userAnswer2);

        // 从数据库中检索第一个用户答案
        UserAnswer userAnswerOne = userAnswerService.getOne(
                Wrappers.lambdaQuery(UserAnswer.class).eq(UserAnswer::getAppId, 1L)
        );
        // 打印第一个用户答案
        System.out.println("UserAnswerOne: " + JSONUtil.toJsonStr(userAnswerOne));

        // 从数据库中检索第二个用户答案
        UserAnswer userAnswerTwo = userAnswerService.getOne(
                Wrappers.lambdaQuery(UserAnswer.class).eq(UserAnswer::getAppId, 2L)
        );
        // 打印第二个用户答案
        System.out.println("UserAnswerTwo: " + JSONUtil.toJsonStr(userAnswerTwo));

        // 断言验证
        assertEquals(1L, userAnswerOne.getAppId());
        assertEquals(1L, userAnswerOne.getUserId());
        assertEquals("1", userAnswerOne.getChoices());

        assertEquals(2L, userAnswerTwo.getAppId());
        assertEquals(1L, userAnswerTwo.getUserId());
        assertEquals("2", userAnswerTwo.getChoices());
    }

}