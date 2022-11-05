package com.estate.exception;

/**
 * Data already exists exception is thrown when services tries to overwrite existing data.
 */
public class DataAlreadyExistsException extends Exception {
    /**
     * Instantiates a new Data already exists exception.
     *
     * @param s the s
     */
    public DataAlreadyExistsException(String s) {
        super(s);
    }
}
