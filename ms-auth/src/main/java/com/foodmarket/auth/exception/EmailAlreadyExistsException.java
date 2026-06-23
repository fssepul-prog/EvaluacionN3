package com.foodmarket.auth.exception;
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String msg) { super(msg); }
}
