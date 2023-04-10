package com.ddockddack.global.error.exception;

import com.ddockddack.global.error.ErrorCode;

public class ImageExtensionException extends RuntimeException {
    private ErrorCode errorCode;

    public ImageExtensionException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ImageExtensionException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
