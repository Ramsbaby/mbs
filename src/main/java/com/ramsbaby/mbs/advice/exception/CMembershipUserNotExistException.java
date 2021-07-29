package com.ramsbaby.mbs.advice.exception;

public class CMembershipUserNotExistException extends RuntimeException {
    public CMembershipUserNotExistException(String msg, Throwable t) {
        super(msg, t);
    }

    public CMembershipUserNotExistException(String msg) {
        super(msg);
    }

    public CMembershipUserNotExistException() {
        super();
    }
}