package com.simplebank.user.application.port.in;

import com.simplebank.user.application.port.in.dto.UserInfo;

public interface GetUserInfoUseCase {
    UserInfo execute(Long userId);
}
