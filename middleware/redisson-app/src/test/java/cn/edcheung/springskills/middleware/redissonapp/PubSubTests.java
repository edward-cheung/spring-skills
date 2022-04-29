package cn.edcheung.springskills.middleware.redissonapp;

import org.junit.jupiter.api.Test;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Description PubSubTests
 *
 * @author Edward Cheung
 * @date 2022/4/26
 * @since JDK 1.8
 */
@SpringBootTest
public class PubSubTests {

    @Autowired
    private RedissonClient client;

    @Test
    void contextLoads() {
        RTopic topic = client.getTopic("testTopic");
        topic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence charSequence, String s) {
                System.out.println(charSequence);
                System.out.println(s);
            }
        });
    }

    @Test
    void test() throws InterruptedException {
        // 在其他线程或JVM节点
        RTopic topic = client.getTopic("testTopic");
        long clientsReceivedMessage = topic.publish("test");
    }

}
