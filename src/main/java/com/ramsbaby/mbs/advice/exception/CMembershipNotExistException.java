package com.ramsbaby.mbs.advice.exception;

public class CMembershipNotExistException extends RuntimeException {
    public CMembershipNotExistException(String msg, Throwable t) {
        super(msg, t);
    }

    public CMembershipNotExistException(String msg) {
        super(msg);
    }

    public CMembershipNotExistException() {
        super();
    }
}