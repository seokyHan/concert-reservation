package com.server.concert_reservation.infrastructure.redis.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * RedisCacheManager 빈을 생성하여 애플리케이션의 캐시 설정을 관리함.
     * 기본 캐시 설정과 개별 캐시 설정을 적용할 수 있도록 구성.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper mapper = createObjectMapper();
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig(serializer)) // 기본 캐시 설정 적용
                .withInitialCacheConfigurations(customCacheConfigs(serializer)) // 특정 캐시에 대한 개별 TTL 설정 적용
                .build();
    }

    /**
     * Redis에서 사용할 ObjectMapper 인스턴스를 생성.
     * - JavaTimeModule 등록 → Java 8+ 날짜/시간 타입 직렬화 지원
     * - 알 수 없는 프로퍼티 무시 → JSON 변환 시 예상치 못한 필드로 인한 오류 방지
     * - 다형성 지원 활성화 → 다양한 타입을 안전하게 직렬화/역직렬화
     * - Enum을 문자열로 직렬화 → 가독성을 높이고 일관성 유지
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Java 8 이상의 날짜/시간 API(LocalDateTime 등) 직렬화 지원
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // JSON 역직렬화 시 알 수 없는 필드가 있어도 오류 발생하지 않도록 설정

        // 다형성(Polymorphic) 타입 직렬화 허용
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(Object.class) // 모든 Object 하위 타입 허용
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL // final이 아닌 클래스에 대해 다형성 타입 지정
        );

        // Enum을 직렬화할 때 toString() 값으로 변환하여 저장 (기본값은 ordinal, 즉 숫자)
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

        return mapper;
    }

    /**
     * 기본 캐시 설정을 정의.
     * - TTL(Time-To-Live) 기본값: 1시간
     */
    private RedisCacheConfiguration defaultCacheConfig(GenericJackson2JsonRedisSerializer serializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues() // null 값 캐싱 방지 (의미 없는 캐싱을 막음)
                .entryTtl(Duration.ofMinutes(30)) // 기본 TTL 설정
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // 키를 문자열로 직렬화
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer)); // 값을 JSON 직렬화
    }

    /**
     * 특정 캐시에 대해 개별적인 TTL(Time-To-Live) 정책을 적용할 수 있도록 설정.
     * 예:
     * - "shortLivedCache" → 10분 TTL
     * - "longLivedCache" → 1일 TTL
     */
    private Map<String, RedisCacheConfiguration> customCacheConfigs(GenericJackson2JsonRedisSerializer serializer) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("availableConcertSchedule", defaultCacheConfig(serializer).entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("availableConcertSeats", defaultCacheConfig(serializer).entryTtl(Duration.ofMinutes(10)));

        return cacheConfigurations;
    }
}