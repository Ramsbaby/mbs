package com.ramsbaby.mbs.advice.exception;

public class CMembershipStatusDisabledException extends RuntimeException {
    public CMembershipStatusDisabledException(String msg, Throwable t) {
        super(msg, t);
    }

    public CMembershipStatusDisabledException(String msg) {
        super(msg);
    }

    public CMembershipStatusDisabledException() {
        super();
    }
}