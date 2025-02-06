package com.server.concert_reservation.infrastructure.user.entity;

import com.server.concert_reservation.domain.user.model.User;
import com.server.concert_reservation.infrastructure.db.user.entity.UserEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserEntityTest {

    @DisplayName("User 엔티티를 User 도메인 모델로 변환한다.")
    @Test
    void userEntityCovertToUserDomainTest() {
        //given
        UserEntity userEntity = Instancio.create(UserEntity.class);
        //when
        User user = userEntity.toDomain();

        //then
        assertEquals(userEntity.getId(), user.getId());
        assertEquals(userEntity.getName(), user.getName());

    }

}