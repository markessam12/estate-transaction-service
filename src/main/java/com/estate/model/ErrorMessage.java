package com.estate.model;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Error Message is a class used for sending error messages to the Api requests.
 * The message attribute is filled by thrown exception error message
 */
@XmlRootElement
public class ErrorMessage {
    private String message;
    private int errorCode;

    /**
     * Instantiates a new Error message.
     */
    public ErrorMessage(){}

    /**
     * Instantiates a new Error message.
     *
     * @param message   the exception error message
     * @param errorCode the error code to send in the http response
     */
    public ErrorMessage(String message, int errorCode){
        super();
        this.message = message;
        this.errorCode = errorCode;
    }

    /**
     * Gets message.
     *
     * @return the error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the error message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets error code.
     *
     * @return the error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets error code.
     *
     * @param errorCode the error code
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
