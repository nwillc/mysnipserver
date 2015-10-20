package com.github.nwillc.mysnipserver.rest.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return "Not Found (404): " + getMessage();
    }
}
