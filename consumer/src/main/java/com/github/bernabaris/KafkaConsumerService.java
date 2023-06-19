package com.github.bernabaris;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(clientIdPrefix = "cdc-consumer", topics = "#{'${kafka.topic}'}", groupId = "${kafka.groupId}")
    public void consume(ConsumerRecord<?, String> message) {
        if (!message.value().equalsIgnoreCase("ping")) {
            log.info("{}", message.value());
        }
    }
}