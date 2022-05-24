package com.auth.exception;

public class EmailAlreadyExistsException extends Exception{
    public EmailAlreadyExistsException() {
        super("Email already exists!");
    }
}
