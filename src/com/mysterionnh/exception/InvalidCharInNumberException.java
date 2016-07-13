package com.mysterionnh.exception;

public class InvalidCharInNumberException extends Exception {

    //private static final long serialVersionUID = 1146231189745418767L;

    public InvalidCharInNumberException() {
        super();
    }

    public InvalidCharInNumberException(String message) {
        super(message);
    }

    public InvalidCharInNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCharInNumberException(Throwable cause) {
        super(cause);
    }
}
