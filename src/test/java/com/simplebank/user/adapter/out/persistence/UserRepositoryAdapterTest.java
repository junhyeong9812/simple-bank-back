package com.simplebank.user.adapter.out.persistence;

import com.simplebank.user.domain.User;
import com.simplebank.user.domain.UserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("UserRepositoryAdapter 테스트")
class UserRepositoryAdapterTest {

    @Autowired
    private UserJpaRepository jpaRepository;

    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserRepositoryAdapter(jpaRepository);
    }
    
    @Test
    @DisplayName("사용자명으로 사용자 조회 성공")
    void loadByUsername_success() {
        //Given
        UserJpaEntity entity = UserJpaEntity.builder()
                .username("user1")
                .password("encodedPassword")
                .status(UserStatus.ACTIVE)
                .build();
        jpaRepository.save(entity);

        //When
        Optional<User> result = adapter.loadByUsername("user1");

        //Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("user1");
        assertThat(result.get().getPassword()).isEqualTo("encodedPassword");
        assertThat(result.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("존재하지 않는 사용자명으로 조회 시 빈 Optional 반환")
    void loadByUsername_not_found() {
        //When
        Optional<User> result = adapter.loadByUsername("nonexistent");

        //then
        assertThat(result).isEmpty();

    }

}