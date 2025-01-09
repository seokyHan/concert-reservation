package com.server.concert_reservation.api.user.infrastructure.entity;

import com.server.concert_reservation.api.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @DisplayName("User 엔티티를 User 도메인 모델로 변환한다.")
    @Test
    void userEntityCovertToUserDomainTest() {
        //given
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("홍길동")
                .build();

        //when
        User user = userEntity.toDomain();

        //then
        assertEquals(userEntity.getId(), user.getId());
        assertEquals(userEntity.getName(), user.getName());

    }

}