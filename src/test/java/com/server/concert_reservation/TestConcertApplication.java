package com.server.concert_reservation;

import com.server.concert_reservation.config.RdbTestcontainersConfig;
import org.springframework.boot.SpringApplication;

public class TestConcertApplication {

    public static void main(String[] args) {
        SpringApplication.from(ConcertApplication::main).with(RdbTestcontainersConfig.class).run(args);
    }

}
