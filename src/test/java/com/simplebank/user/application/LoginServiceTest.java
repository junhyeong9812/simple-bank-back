package com.simplebank.user.application;

import com.simplebank.user.application.port.in.dto.LoginCommand;
import com.simplebank.user.application.port.in.dto.LoginResult;
import com.simplebank.user.application.port.out.LoadUserPort;
import com.simplebank.user.domain.User;
import com.simplebank.user.domain.UserStatus;
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

}