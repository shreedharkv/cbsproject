package com.dfq.coeffi.cbs.exception;

/**
 * @Auther : H Kapil Kumar
 * @Date : May-18
 */
public class CbsException extends RuntimeException {
    private static final long serialVersionUID = -7170697018482052890L;

    public CbsException(String message) {
        super(message);
    }

    public CbsException() {
        super("Something went wrong on academic application. Please contact");
    }

}