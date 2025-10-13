package com.simplebank.user.adapter.in.web;

import com.simplebank.user.adapter.in.web.dto.LoginRequest;
import com.simplebank.user.adapter.in.web.dto.LoginResponse;
import com.simplebank.user.application.port.in.LoginUseCase;
import com.simplebank.user.application.port.in.dto.LoginCommand;
import com.simplebank.user.application.port.in.dto.LoginResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(
                request.getUsername(),
                request.getPassword()
        );

        LoginResult result = loginUseCase.execute(command);

        return ResponseEntity.ok(new LoginResponse(result.getUserId(), result.getUsername()));
    }
}
