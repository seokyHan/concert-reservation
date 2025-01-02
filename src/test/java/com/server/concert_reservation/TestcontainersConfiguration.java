package com.server.concert_reservation;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
class TestcontainersConfiguration {

	public static final MariaDBContainer<?> MARIADB_CONTAINER;

	static {
		MARIADB_CONTAINER = new MariaDBContainer<>(DockerImageName.parse("mariadb:11.6.2"))
				.withDatabaseName("concert")
				.withUsername("test")
				.withPassword("test");
		MARIADB_CONTAINER.start();

		System.setProperty("spring.datasource.url", MARIADB_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", MARIADB_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MARIADB_CONTAINER.getPassword());
	}

	@PreDestroy
	public void preDestroy() {
		if (MARIADB_CONTAINER.isRunning()) {
			MARIADB_CONTAINER.stop();
		}
	}

}
