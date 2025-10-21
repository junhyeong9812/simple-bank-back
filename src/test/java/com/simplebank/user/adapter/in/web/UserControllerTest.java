package com.simplebank.user.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplebank.user.adapter.in.web.dto.LoginRequest;
import com.simplebank.user.application.port.in.GetUserInfoUseCase;
import com.simplebank.user.application.port.in.LoginUseCase;
import com.simplebank.user.application.port.in.dto.LoginCommand;
import com.simplebank.user.application.port.in.dto.LoginResult;
import com.simplebank.user.application.port.in.dto.UserInfo;
import com.simplebank.user.domain.exception.BlockedUserException;
import com.simplebank.user.domain.exception.InvalidPasswordException;
import com.simplebank.user.domain.exception.UserNotFoundException;
import org.apache.juli.logging.Log;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@WebMvcTest(UserController.class)
@DisplayName("UserController 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private GetUserInfoUseCase getUserInfoUseCase;

    @Test
    @DisplayName("POST /api/users/login - 로그인 성공")
    void login_success() throws Exception {
        //given
        LoginRequest request = new LoginRequest("userId","userPassword");
        LoginResult result = new LoginResult(1L, "userId");

        when(loginUseCase.execute(any(LoginCommand.class)))
                .thenReturn(result);

        //when& then
        mockMvc.perform(
                post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("userId"));
    }

    @Test
    @DisplayName("POST /api/users/login - 잘못된 비밀번호")
    void login_fail_invalid_password() throws Exception {
        //Given
        LoginRequest request = new LoginRequest("userId","wrongPassword");

        when(loginUseCase.execute(any(LoginCommand.class)))
                .thenThrow(new InvalidPasswordException());

        //When&Then
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/users/login - 존재하지 않는 사용자")
    void login_fail_user_not_found() throws Exception {
        //given
        LoginRequest request = new LoginRequest("nonexistent", "password123");

        when(loginUseCase.execute(any(LoginCommand.class)))
                .thenThrow(new UserNotFoundException("nonexistent"));

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("POST /api/users/login - 차단된 사용자")
    void login_fail_blocked_user() throws Exception {
        //Given
        LoginRequest request = new LoginRequest("blockedUser", "password123");

        when(loginUseCase.execute(any(LoginCommand.class)))
                .thenThrow(new BlockedUserException("blockedUser"));

        //when
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @DisplayName("GET /api/users/{userId} - 사용자 정보 조회 성공")
    void getUserInfo_success() throws Exception {
        //Given
        Long userId = 1L;
        UserInfo userInfo = new UserInfo(1L, "user1", "ACTIVE");

        when(getUserInfoUseCase.execute(userId))
                .thenReturn(userInfo);

        //When & Then
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

}