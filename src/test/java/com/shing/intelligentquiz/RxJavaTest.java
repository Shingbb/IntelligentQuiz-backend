package com.shing.intelligentquiz;

import com.shing.intelligentquiz.common.TestBase;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author shing
 */
class RxJavaTest extends TestBase {

    @Test
    void RxJavaTestDemo() throws InterruptedException {
        // 使用 CountDownLatch 来控制主线程的等待
        CountDownLatch latch = new CountDownLatch(1);

        // 创建数据流
        Flowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS)
                .map(i -> i + 1)
                .take(10)  // 限制数据流到10个事件
                .subscribeOn(Schedulers.io());

        // 订阅 Flowable 流，并且打印出每个接收到的数字
        flowable.observeOn(Schedulers.io())
                .doOnNext(item -> System.out.println(item.toString()))
                .doOnComplete(latch::countDown)  // 完成后减少 latch
                .subscribe();

        // 等待流完成
        latch.await();
    }
}