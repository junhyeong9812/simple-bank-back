package com.simplebank.user.adapter.in.web;

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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginCommand = new LoginCommand(
                request.getUsername(),
                request.getPassword()
        );

        LoginResult result = loginUseCase.execute(comand);

        return ResponseEntity.ok(new LoginResponse(result.getUserId(), result.getUsername()));
    }
}
