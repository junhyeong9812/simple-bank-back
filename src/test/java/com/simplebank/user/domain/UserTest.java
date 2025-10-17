package com.simplebank.user.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User 도메인 테스트")
class UserTest {
    
    @Test
    @DisplayName("User 생성 성공")
    void create_user() {
        //when
        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("password123")
                .status(UserStatus.ACTIVE)
                .build();

        //Then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("user1");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("활성 상태 사용자 확인")
    void isActive_when_status_active() {
        //Given
        User user = User.builder()
                .status(UserStatus.ACTIVE)
                .build();

        //When & Then
        assertThat(user.isActive()).isTrue();
    }



}