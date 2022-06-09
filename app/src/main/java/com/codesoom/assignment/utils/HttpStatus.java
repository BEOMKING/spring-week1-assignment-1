package com.codesoom.assignment.utils;

public enum HttpStatus {
    OK(200),
    CREATED(201),
    NO_CONTENT(204),
    BAD_REQUEST(400),
    NOT_FOUND(404);

    private final Integer statusCode;

    HttpStatus(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer statusCode() { return statusCode; }
}
