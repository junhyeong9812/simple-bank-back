package com.simplebank.user.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfo {
    private final Long userId;
    private final String username;
    private final String status;
}
