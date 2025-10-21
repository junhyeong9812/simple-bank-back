package com.simplebank.user.application;

import com.simplebank.user.application.port.in.dto.UserInfo;
import com.simplebank.user.application.port.out.LoadUserPort;
import com.simplebank.user.domain.User;
import com.simplebank.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserInfoService 테스트")
class GetUserInfoServiceTest {

    @Mock
    private LoadUserPort loadUserPort;

    @InjectMocks
    private GetUserInfoService getUserInfoService;

    @Test
    @DisplayName("사용자 ID로 사용자 정보 조회 성공")
    void getUserInfo_success() {
        //Given
        Long userId = 1L;
        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("encodedPassword")
                .status(UserStatus.ACTIVE)
                .build();

        when(loadUserPort.loadById(userId))
                .thenReturn(Optional.of(user));

        //When
        UserInfo result = getUserInfoService.execute(userId);

        //Then
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("user1");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        verify(loadUserPort).loadById(userId);
    }


}