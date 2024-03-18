package com.ssafy.jariyo.global.jwt.util;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorMessage {

    UNKNOWN_ERROR(400, "unknown_error"),
    NO_VALUE_PRESENT(400, "No value present"),
    MISSING_PARTS(400, "The token was expected to have 3 parts, but got 2"),
    EXPIRED_TOKEN(401, "The Token has expired"),
    BLACKLISTED_TOKENS(401, "Blacklisted tokens"),
    ACCESS_DENIED(401, "access_denied")
    ;

    private final int code;
    private final String message;

    ErrorMessage(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
