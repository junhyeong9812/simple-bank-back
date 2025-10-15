package com.simplebank.user.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super(String.format("사용자를 찾을 수 없습니다: %s", username));
    }
}
