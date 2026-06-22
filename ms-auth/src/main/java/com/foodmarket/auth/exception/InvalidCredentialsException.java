package com.foodmarket.auth.exception;
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String msg) { super(msg); }
}
