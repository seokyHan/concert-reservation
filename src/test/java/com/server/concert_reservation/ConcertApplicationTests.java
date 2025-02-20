package com.server.concert_reservation;

import com.server.concert_reservation.config.KafkaTestContainerConfig;
import com.server.concert_reservation.config.RdbTestcontainersConfig;
import com.server.concert_reservation.config.RedisTestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(value = {
        RdbTestcontainersConfig.class,
        RedisTestContainerConfig.class,
        KafkaTestContainerConfig.class
})
@SpringBootTest
class ConcertApplicationTests {

    @Test
    void contextLoads() {
    }

}
