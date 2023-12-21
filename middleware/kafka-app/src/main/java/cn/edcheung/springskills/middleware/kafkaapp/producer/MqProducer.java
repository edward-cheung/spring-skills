package cn.edcheung.springskills.middleware.kafkaapp.producer;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

@Service
public class MqProducer implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(MqProducer.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${mq.topic.order}")
    private String orderTopic;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 统一设置消息发送结果回调处理
        kafkaTemplate.setProducerListener(new ProducerListener<String, Object>() {
            @Override
            public void onSuccess(ProducerRecord<String, Object> producerRecord, RecordMetadata recordMetadata) {
                if (logger.isDebugEnabled()) {
                    logger.debug("ok,message={}", producerRecord.value());
                }
            }

            @Override
            public void onError(ProducerRecord<String, Object> producerRecord, RecordMetadata recordMetadata, Exception exception) {
                logger.error("error!message={}", producerRecord.value(), exception);
            }
        });
    }

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
        kafkaTemplate.send(orderTopic, String.valueOf(order.getId()), JSON.toJSONString(order));
        // 指定分区发送
        kafkaTemplate.send(orderTopic, 0, String.valueOf(order.getId()), JSON.toJSONString(order));
    }

    /**
     * 使用同步发送
     */
    public boolean syncSendMsg(Long orderId) {
        // 1 构建消息数据
        Order order = new Order(orderId, "订单-" + orderId, 1000.00);

        // 2.发送消息
        // 未指定分区发送
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(orderTopic, String.valueOf(order.getId()), JSON.toJSONString(order));
        SendResult<String, Object> sendResult = null;
        try {
            // 注意，可以设置等待时间，超出后，不再等候结果
            sendResult = future.get();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        RecordMetadata metadata = sendResult.getRecordMetadata();
        ProducerRecord<String, Object> producerRecord = sendResult.getProducerRecord();
        System.out.println("同步方式发送消息结果：" + "topic-" + metadata.topic() + "|partition-" + metadata.partition() + "|offset-" + metadata.offset());
        return true;
    }

    private static class Order implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long id;
        private String desc;
        private Double amount;

        public Order(Long id, String desc, Double amount) {
            this.id = id;
            this.desc = desc;
            this.amount = amount;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }
}
