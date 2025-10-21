package com.simplebank.user.application;

import com.simplebank.user.application.port.in.GetUserInfoUseCase;
import com.simplebank.user.application.port.in.dto.UserInfo;
import com.simplebank.user.application.port.out.LoadUserPort;
import com.simplebank.user.domain.User;
import com.simplebank.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserInfoService implements GetUserInfoUseCase {

    private final LoadUserPort loadUserPort;

    @Override
    public UserInfo execute(Long userId) {
        //1. 사용자 조회
        User user = loadUserPort.loadById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        
        //2. 결과 반환
        return new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getStatus().name()
        );
        
    }
}
