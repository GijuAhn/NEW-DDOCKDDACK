package com.ddockddack.global.error.exception;

import com.ddockddack.global.error.ErrorCode;

public class AlreadyExistResourceException extends RuntimeException {
    private ErrorCode errorCode;

    public AlreadyExistResourceException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AlreadyExistResourceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
