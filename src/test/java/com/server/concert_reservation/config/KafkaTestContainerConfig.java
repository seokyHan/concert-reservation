package com.server.concert_reservation.config;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class KafkaTestContainerConfig {

    @Container
    static ConfluentKafkaContainer KAFKA_CONTAINER;

    static {
        DockerImageName imageName = DockerImageName.parse("confluentinc/cp-kafka:latest")
                .asCompatibleSubstituteFor("apache/kafka");
        KAFKA_CONTAINER = new ConfluentKafkaContainer(imageName);

        KAFKA_CONTAINER.start();
        System.setProperty("spring.kafka.bootstrap-servers", KAFKA_CONTAINER.getBootstrapServers());
    }

    @PreDestroy
    public void preDestroy() {
        if (KAFKA_CONTAINER.isRunning()) {
            KAFKA_CONTAINER.stop();
        }
    }
}
