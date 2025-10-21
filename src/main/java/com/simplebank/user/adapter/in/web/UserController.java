package com.simplebank.user.adapter.in.web;

import com.simplebank.user.adapter.in.web.dto.LoginRequest;
import com.simplebank.user.adapter.in.web.dto.LoginResponse;
import com.simplebank.user.adapter.in.web.dto.UserInfoResponse;
import com.simplebank.user.application.port.in.GetUserInfoUseCase;
import com.simplebank.user.application.port.in.LoginUseCase;
import com.simplebank.user.application.port.in.dto.LoginCommand;
import com.simplebank.user.application.port.in.dto.LoginResult;
import com.simplebank.user.application.port.in.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final LoginUseCase loginUseCase;
    private final GetUserInfoUseCase getUserInfoUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(
                request.getUsername(),
                request.getPassword()
        );

        LoginResult result = loginUseCase.execute(command);

        return ResponseEntity.ok(new LoginResponse(result.getUserId(), result.getUsername()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable Long userId) {
        UserInfo userInfo = getUserInfoUseCase.execute(userId);

        return ResponseEntity.ok(new UserInfoResponse(
                userInfo.getUserId(),
                userInfo.getUsername(),
                userInfo.getStatus()
        ));
    }
}
