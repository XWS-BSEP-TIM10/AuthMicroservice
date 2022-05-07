package com.auth.exception;

public class UserAlreadyExistsException extends Exception{
    public UserAlreadyExistsException() {
        super("User already exists!");
    }
}
