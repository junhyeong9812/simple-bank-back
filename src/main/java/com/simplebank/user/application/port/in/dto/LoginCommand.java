package com.simplebank.user.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginCommand {
    private final String username;
    private final String password;
}
