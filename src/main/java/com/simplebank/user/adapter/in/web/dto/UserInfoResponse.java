package com.simplebank.user.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private final Long userId;
    private final String username;
    private final String status;
}
