package com.estate.exception;

/**
 * Data not found exception is thrown when a service trying to access non-existing data.
 */
public class DataNotFoundException extends Exception {
    /**
     * Instantiates a new Data not found exception.
     *
     * @param s the s
     */
    public DataNotFoundException(String s) {
        super(s);
    }
}
