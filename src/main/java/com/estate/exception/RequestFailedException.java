package com.estate.exception;

/**
 * Request failed exception is thrown when a request failed some preconditions.
 */
public class RequestFailedException extends Exception {
    /**
     * Instantiates a new Request failed exception.
     *
     * @param s the error message string
     */
    public RequestFailedException(String s) {
        super(s);
    }
}
