package com.github.bernabaris.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class KafkaProducerService {

    @Value(value = "${kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void init() {
        log.info("{} is started", this.getClass().getSimpleName());
    }

    public void sendMessage(final String message) {
        log.info("Sending message: '{}' - topic: '{}'", topic, message);
        try {
            ListenableFuture future = this.kafkaTemplate.send(topic, message);
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

                @Override
                public void onSuccess(final SendResult<String, String> message) {
                    log.info("sent message= {} with offset= {}", message, message.getRecordMetadata().offset());
                }

                @Override
                public void onFailure(final Throwable throwable) {
                    log.error("unable to send message= {}, ERROR: {}", message, throwable.getMessage());
                }
            });
            future.get(10000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Exception occurred while sending kafka message: {}", message, e);
        }
    }
}
