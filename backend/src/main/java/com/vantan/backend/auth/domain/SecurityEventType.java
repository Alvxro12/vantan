package com.vantan.backend.auth.domain;

public enum SecurityEventType {
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    LOGOUT,
    TOKEN_REUSE_DETECTED,
    FAMILY_INVALIDATED,
    RATE_LIMIT_TRIGGERED
}