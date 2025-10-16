package com.simplebank.user.application;

import com.simplebank.user.application.port.in.LoginUseCase;
import com.simplebank.user.application.port.in.dto.LoginCommand;
import com.simplebank.user.application.port.in.dto.LoginResult;
import com.simplebank.user.application.port.out.LoadUserPort;
import com.simplebank.user.domain.User;
import com.simplebank.user.domain.exception.InvalidPasswordException;
import com.simplebank.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService implements LoginUseCase {

    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResult execute(LoginCommand command){
        // 1. 사용자 조회
        User user = loadUserPort.loadByUsername(command.getUsername())
                .orElseThrow(() -> new UserNotFoundException(command.getUsername()));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())){
            throw new InvalidPasswordException();
        }

        // 3. 결과 반환
        return new LoginResult(user.getId(), user.getUsername());
    }
}
