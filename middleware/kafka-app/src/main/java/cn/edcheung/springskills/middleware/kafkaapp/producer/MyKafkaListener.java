package cn.edcheung.springskills.middleware.kafkaapp.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;

import javax.annotation.PostConstruct;

@Configuration
public class MyKafkaListener {
    private final static Logger logger = LoggerFactory.getLogger(MyKafkaListener.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 配置发送回调监听
     */
    @PostConstruct
    private void listener() {
        kafkaTemplate.setProducerListener(new ProducerListener<String, Object>() {
            @Override
            public void onSuccess(ProducerRecord<String, Object> producerRecord, RecordMetadata recordMetadata) {
                logger.info("ok,message={}", producerRecord.value());
            }

            @Override
            public void onError(ProducerRecord<String, Object> producerRecord, RecordMetadata recordMetadata, Exception exception) {
                logger.error("error!message={}", producerRecord.value());
            }
        });
    }
}

