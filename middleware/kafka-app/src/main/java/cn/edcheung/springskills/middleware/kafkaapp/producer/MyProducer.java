package cn.edcheung.springskills.middleware.kafkaapp.producer;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

@Service
public class MyProducer {

    private final static String TOPIC_NAME = "my-springboot-topic";

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * KafkaTemplate调用send时默认采用异步发送
     */
    public void sendMsg(Long orderId) {
        // 1 构建消息数据
        Order order = new Order(orderId, "订单-" + orderId, 1000.00);

        // 2.发送消息

        // 分区策略决定了消息根据key投放到哪个分区，也是顺序消费保障的基石。
        // - 给定了分区号，直接将数据发送到指定的分区里面去
        // - 没有给定分区号，给定数据的key值，通过key取上hashCode进行分区
        // - 既没有给定分区号，也没有给定key值，直接轮循进行分区（默认）
        // - 自定义分区发送

        // 未指定分区发送
        kafkaTemplate.send(TOPIC_NAME, String.valueOf(order.getId()), JSON.toJSONString(order));
        // 指定分区发送
        kafkaTemplate.send(TOPIC_NAME, 0, String.valueOf(order.getId()), JSON.toJSONString(order));
    }

    /**
     * 使用同步发送
     */
    public void syncSendMsg(Long orderId) {
        //1 构建消息数据
        Order order = new Order(orderId, "订单-" + orderId, 1000.00);

        //2.发送消息
        // 未指定分区发送
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_NAME, String.valueOf(order.getId()), JSON.toJSONString(order));
        SendResult<String, String> sendResult = null;
        try {
            // 注意，可以设置等待时间，超出后，不再等候结果
            sendResult = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        RecordMetadata metadata = sendResult.getRecordMetadata();
        ProducerRecord<String, String> producerRecord = sendResult.getProducerRecord();
        System.out.println("同步方式发送消息结果：" + "topic-" + metadata.topic() + "|partition-" + metadata.partition() + "|offset-" + metadata.offset());
    }
}
