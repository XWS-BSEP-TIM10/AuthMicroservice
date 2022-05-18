package com.auth.exception;

public class RepeatedPasswordNotMatchingException extends Exception{
    public RepeatedPasswordNotMatchingException() {
        super("Repeated password not matching!");
    }
}
