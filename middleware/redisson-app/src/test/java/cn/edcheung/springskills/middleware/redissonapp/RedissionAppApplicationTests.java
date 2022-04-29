package cn.edcheung.springskills.middleware.redissonapp;

import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class RedissionAppApplicationTests {

    @Autowired
    private RedissonClient client;

    @Test
    void contextLoads() {
        // 什么时候需要分布式锁？
        // 加、解锁的代码位置有讲究么？
        // 如何避免出现锁再也无法删除？「」
        // 超时时间设置多少合适呢？
        // 如何避免锁被其他线程释放
        // 如何实现重入锁？
        // 主从架构会带来什么安全问题？
        // 什么是 Redlock
        // Redisson 分布式锁最佳实战
        // 看门狗实现原理
    }

    @Test
    void test1() throws InterruptedException {
        // 可重入锁（Reentrant Lock）
        // RLock对象完全符合Java的Lock规范。就是说只有拥有锁的进程才能解锁，其他进程解锁则会抛出IllegalMonitorStateException错误。
        // 1
        RLock lock = client.getLock(Thread.currentThread().getName());
        try {

            // 1.最常用的第一种写法：失败无限重试
            // 拿锁失败时会不停的重试，具有Watch Dog 自动延期机制，默认续30s 每隔30/3=10 秒续到30s
            lock.lock();

            // 2.失败超时重试，自动续命
            // 尝试拿锁10s后停止重试,获取失败返回false，具有Watch Dog 自动延期机制， 默认续30s
            boolean flag = lock.tryLock(10, TimeUnit.SECONDS);

            // 3.超时自动释放锁
            // 没有Watch Dog ，10s后自动释放,不需要调用 unlock 释放锁。
            lock.lock(10, TimeUnit.SECONDS);

            // 执行业务逻辑
            // ...
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        // 3. 尝试加锁，最多等待100秒，上锁以后10秒自动解锁,没有 Watch dog
        boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
        if (res) {
            try {
                // ...
            } finally {
                lock.unlock();
            }
        }
    }
}
