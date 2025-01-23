package com.server.concert_reservation;

import com.server.concert_reservation.config.RdbTestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(RdbTestcontainersConfig.class)
@SpringBootTest
class ConcertApplicationTests {

    @Test
    void contextLoads() {
    }

}
