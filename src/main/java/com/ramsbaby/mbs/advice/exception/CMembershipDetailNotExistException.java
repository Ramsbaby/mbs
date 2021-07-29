package com.ramsbaby.mbs.advice.exception;

public class CMembershipDetailNotExistException extends RuntimeException {
    public CMembershipDetailNotExistException(String msg, Throwable t) {
        super(msg, t);
    }

    public CMembershipDetailNotExistException(String msg) {
        super(msg);
    }

    public CMembershipDetailNotExistException() {
        super();
    }
}