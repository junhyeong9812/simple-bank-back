package com.simplebank.user.adapter.out.persistence;

import com.simplebank.user.domain.User;
import com.simplebank.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("UserJpaEntity 테스트")
class UserJpaEntityTest {

    @Test
    @DisplayName("Domain에서 JPA Entity로 변환")
    void fromConvertsDomainToEntity() {
        //Given
        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("encodedPassword")
                .status(UserStatus.ACTIVE)
                .build();

        //When
        UserJpaEntity entity = UserJpaEntity.from(user);

        //Then
        assertEquals(user.getUsername(), entity.getUsername());
        assertEquals(user.getPassword(), entity.getPassword());
        assertEquals(user.getStatus(), entity.getStatus());
    }

    @Test
    @DisplayName("JPA Entity에서 Domain으로 변환")
    void toDomainConvertsEntityToDomain() {
        //Given
        UserJpaEntity entity = UserJpaEntity.builder()
                .id(1L)
                .username("user1")
                .password("encodedPassword")
                .status(UserStatus.ACTIVE)
                .build();

        //When
        User user = entity.toDomain();

        //Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("user1");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}