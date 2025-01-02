package com.server.concert_reservation;

import org.springframework.boot.SpringApplication;

public class TestConcertApplication {

	public static void main(String[] args) {
		SpringApplication.from(ConcertApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
