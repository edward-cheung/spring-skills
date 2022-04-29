package cn.edcheung.springskills.middleware.redissonapp;

import org.junit.jupiter.api.Test;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

/**
 * Description RateLimiterTests
 *
 * @author Edward Cheung
 * @date 2022/4/26
 * @since JDK 1.8
 */
@SpringBootTest
public class RateLimiterTests {

    @Autowired
    private RedissonClient client;

    @Test
    void contextLoads() {
    }

    @Test
    void test() throws InterruptedException {
        RRateLimiter rateLimiter = client.getRateLimiter("rateLimiter");
        // 初始化
        // 最大流速 = 每1秒钟产生2个令牌
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        CountDownLatch latch = new CountDownLatch(1);
        Runnable runnable = () -> {
            rateLimiter.acquire();
            latch.countDown();
            // ...
        };
        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);
        t1.start();
        t2.start();
        latch.await();

        boolean res = rateLimiter.tryAcquire();
        if (res) {
            System.out.println(" current thread acquired a token successfully");
            // ...
        } else {
            System.out.println(" current thread failed to acquired a token");
            // ...
        }

    }

}
