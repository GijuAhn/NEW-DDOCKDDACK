package com.ddockddack.global.error.exception;

import com.ddockddack.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class NumberOfFileExceedException extends RuntimeException {

    private ErrorCode errorCode;

    public NumberOfFileExceedException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public NumberOfFileExceedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
