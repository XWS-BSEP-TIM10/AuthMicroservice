package com.auth.exception;

public class TokenNotFoundException extends Exception{
    public TokenNotFoundException() {
        super("Token not found!");
    }
}
