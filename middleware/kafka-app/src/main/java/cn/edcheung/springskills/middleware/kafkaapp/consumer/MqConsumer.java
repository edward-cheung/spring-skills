package cn.edcheung.springskills.middleware.kafkaapp.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MqConsumer {

    /**
     * 如果一个主题要被多个消费组消费，那么我们使用 @KafkaListener 注解来注入多个消费者即可。
     * <p>
     * group-id：表示消费组，如果没有指定，则会使用配置文件中设置的默认的groupId
     * topicPartitions：一个消费组可以消费多个主题分区
     * TopicPartition：主题分区相关
     * concurrency：同组下的消费者个数，必须小于等于分区总数，大于意义不大，没必要大于
     */
    @KafkaListener(topics = "${mq.topic.order}", groupId = "orderConsumerGroup1")
    public void orderConsumerGroup1(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String value = record.value();
        System.out.println(value);
        System.out.printf("ConsumerGroup1收到消息：partition = %d,offset = %d, key = %s, value = %s%n", record.partition(), record.offset(), record.key(), value);
        // 手动提交offset
        ack.acknowledge();
    }

    /**
     * 配置多个消费组，同时批量消费
     */
    @KafkaListener(topics = "${mq.topic.order}", groupId = "orderConsumerGroup2")
    public void orderConsumerGroup2(List<ConsumerRecord<String, String>> recordList, Acknowledgment ack) {
        for (ConsumerRecord<String, String> record : recordList) {
            String value = record.value();
            System.out.println(value);
            System.out.printf("ConsumerGroup2收到消息：partition = %d,offset = %d, key = %s, value = %s%n", record.partition(), record.offset(), record.key(), value);
        }
        // 手动提交offset
        ack.acknowledge();
    }
}

