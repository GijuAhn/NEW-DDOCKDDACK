package com.ddockddack.global.error.exception;

import com.ddockddack.global.error.ErrorCode;
import org.springframework.security.core.AuthenticationException;

public class AccessDeniedException extends AuthenticationException {
    private ErrorCode errorCode;

    public AccessDeniedException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
