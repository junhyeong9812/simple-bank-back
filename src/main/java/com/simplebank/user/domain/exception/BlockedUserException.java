package com.simplebank.user.domain.exception;

public class BlockedUserException extends RuntimeException{
    public BlockedUserException(String username) {
        super(String.format("차단된 사용자입니다: %s", username));
    }
}
