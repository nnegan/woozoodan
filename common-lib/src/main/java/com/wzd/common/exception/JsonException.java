package com.wzd.common.exception;

public class JsonException extends RuntimeException {
    private static final long serialVersionUID = 8158934958358565325L;

    public JsonException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
