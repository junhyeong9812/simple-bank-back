package com.simplebank.user.application;

import com.simplebank.user.application.port.in.dto.LoginCommand;
import com.simplebank.user.application.port.in.dto.LoginResult;
import com.simplebank.user.application.port.out.LoadUserPort;
import com.simplebank.user.domain.User;
import com.simplebank.user.domain.UserStatus;
import com.simplebank.user.domain.exception.BlockedUserException;
import com.simplebank.user.domain.exception.InvalidPasswordException;
import com.simplebank.user.domain.exception.UserNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 테스트")
class LoginServiceTest {

    @Mock
    private LoadUserPort loadUserPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    @Test
    @DisplayName("올바른 사용자명과 비밀번호로 로그인 성공")
    void login_success(){
        //Given
        LoginCommand command = new LoginCommand("user1","password123");

        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("encodedPassword")
                .status(UserStatus.ACTIVE)
                .build();

        when(loadUserPort.loadByUsername("user1"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123","encodedPassword"))
                .thenReturn(true);

        //when
        LoginResult result = loginService.execute(command);

        //then
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("user1");
        verify(loadUserPort).loadByUsername("user1");
    }

    @Test
    @DisplayName("존재하지 않는 사용자명으로 로그인 실패")
    void login_fail_user_not_found(){
        //Given
        LoginCommand command = new LoginCommand("nonexistent","pasword123");

        when(loadUserPort.loadByUsername("nonexistent"))
                .thenReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> loginService.execute(command))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("nonexistent");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패")
    void login_fail_invalid_password() {
        //Given
        LoginCommand command = new LoginCommand("user1", "wrongPassword");

        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("encodedPassword")
                .status(UserStatus.ACTIVE)
                .build();

        when(loadUserPort.loadByUsername("user1"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword","encodedPassword"))
                .thenReturn(false);

        //when & then
        assertThatThrownBy(() -> loginService.execute(command))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    @DisplayName("차단된 사용자 로그인 실패")
    void login_fail_blocked_user() {
        //Given
        LoginCommand command = new LoginCommand("blockedUser", "password123");

        User user = User.builder()
                .id(1L)
                .username("blockedUser")
                .password("encodedPassword")
                .status(UserStatus.BLOCKED)
                .build();

        when(loadUserPort.loadByUsername("blockedUser"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123","encodedPassword"))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> loginService.execute(command))
                .isInstanceOf(BlockedUserException.class)
                .hasMessageContaining("blockedUser");
    }
}