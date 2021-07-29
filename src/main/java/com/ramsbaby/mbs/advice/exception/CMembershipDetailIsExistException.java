package com.ramsbaby.mbs.advice.exception;

public class CMembershipDetailIsExistException extends RuntimeException {
    public CMembershipDetailIsExistException(String msg, Throwable t) {
        super(msg, t);
    }

    public CMembershipDetailIsExistException(String msg) {
        super(msg);
    }

    public CMembershipDetailIsExistException() {
        super();
    }
}