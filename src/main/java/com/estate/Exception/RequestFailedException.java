package com.estate.Exception;

public class RequestFailedException extends RuntimeException {
    public RequestFailedException(String s) {
        super(s);
    }
}
