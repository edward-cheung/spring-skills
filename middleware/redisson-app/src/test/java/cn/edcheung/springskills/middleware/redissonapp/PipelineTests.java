package cn.edcheung.springskills.middleware.redissonapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.redisson.api.BatchResult;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Description PipelineTests
 *
 * @author Edward Cheung
 * @date 2022/4/26
 * @since JDK 1.8
 */
@SpringBootTest
public class PipelineTests {

    @Autowired
    private RedissonClient client;

    @Test
    void contextLoads() {
    }

    @Test
    void test() throws InterruptedException, JsonProcessingException {
        RBatch batch = client.createBatch();
        batch.getMap("test").fastPutAsync("1", "2");
        batch.getMap("test").fastPutAsync("2", "3");
        batch.getMap("test").putAsync("2", "5");
        BatchResult<?> res = batch.execute();
        List<?> responses = res.getResponses();
        System.out.println(" a batch of commands executes end. result: " +
                new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(responses));
        System.out.println(" a batch of commands executes end. synced slaves: " + res.getSyncedSlaves());
    }

}
