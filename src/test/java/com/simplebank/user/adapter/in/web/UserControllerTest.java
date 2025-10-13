package com.simplebank.user.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(UserController.class)
@DisplayName("UserController 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoginUseCase loginUseCase;

    @Test
    @DisplayName("POST /api/users/login - 로그인 성공")
    void login_success() throws Exception {
        //given
        LoginRequest request = new LoginRequest("userId","userPassword");
        LoginResult result = new LoginResult(1L, "userId");

        when(loginUseCase.excute(any(LoginCommand.class)))
                .thenReturn(result);

        //when& then
        mockMvc.perform(post("/api/users/login"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("userId"));
    }

}