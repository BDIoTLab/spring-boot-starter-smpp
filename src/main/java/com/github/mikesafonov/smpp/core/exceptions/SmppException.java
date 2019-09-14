package com.github.mikesafonov.smpp.core.exceptions;

/**
 * @author Mike Safonov
 */
public class SmppException extends RuntimeException {

    protected final int errorCode;
    protected final String errorMessage;

    public SmppException(int errorCode, String errorMessage){

        super(errorMessage);

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
