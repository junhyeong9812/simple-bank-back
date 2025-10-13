package com.simplebank.user.application.port.in;

import com.simplebank.user.application.port.in.dto.LoginCommand;
import com.simplebank.user.application.port.in.dto.LoginResult;

public interface LoginUseCase {
    LoginResult execute(LoginCommand command);
}
