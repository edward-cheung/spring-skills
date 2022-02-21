package cn.edcheung.springskills.util.idworker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class LocalIdWorkerTests {

    @Test
    void contextLoads() {
    }

    @Test
    void LocalIdWorkerTest() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10,
                10,
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(128),
                new ThreadPoolExecutor.DiscardPolicy());
        // 开100个线程
        LocalIdWorker worker = new LocalIdWorker();
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                for (int j = 0; j < 10; j++) {
                    System.out.println(Thread.currentThread().getName() + ":" + worker.nextId());
                }
            });
        }
        executor.shutdown();
    }
}
