package com.auth.exception;

public class PasswordsNotMatchingException extends Exception{
    public PasswordsNotMatchingException() {
        super("Passwords not matching!");
    }
}
