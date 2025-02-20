//package com.server.concert_reservation.infrastructure.kafka.config;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.listener.ContainerProperties;
//import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
//import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@EnableKafka
//@Configuration
//public class KafkaConfig {
//    private static final int MAX_POLLING_SIZE = 10;
//    private static final int FETCH_MIN_BYTES = 1;
//    private static final int FETCH_MAX_WAIT_MS = 500;
//    private static final int SESSION_TIMEOUT_MS = 15000;
//    private static final int HEARTBEAT_INTERVAL_MS = 5000;
//    private static final int MAX_POLL_INTERVAL_MS = 300000;
//
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, Object> batchListenerContainerFactory(
//            ByteArrayJsonMessageConverter converter,
//            ConsumerFactory<String, Object> kafkaConsumerFactory) {
//
//        Map<String, Object> properties = new HashMap<>(kafkaConsumerFactory.getConfigurationProperties());
//        properties.putAll(Map.of(
//                ConsumerConfig.MAX_POLL_RECORDS_CONFIG, MAX_POLLING_SIZE,
//                ConsumerConfig.FETCH_MIN_BYTES_CONFIG, FETCH_MIN_BYTES,
//                ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, FETCH_MAX_WAIT_MS,
//                ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, SESSION_TIMEOUT_MS,
//                ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, HEARTBEAT_INTERVAL_MS,
//                ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, MAX_POLL_INTERVAL_MS
//        ));
//
//        DefaultKafkaConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(properties);
//
//        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
//        factory.setBatchMessageConverter(new BatchMessagingMessageConverter(converter));
//        factory.setConcurrency(3);
//        factory.setBatchListener(true);
//
//        return factory;
//    }
//
//    @Bean
//    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties kafkaProperties) {
//        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
//    }
//
//    @Bean
//    public ByteArrayJsonMessageConverter byteArrayJsonMessageConverter() {
//        return new ByteArrayJsonMessageConverter();
//    }
//
//}
