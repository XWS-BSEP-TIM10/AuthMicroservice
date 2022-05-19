package com.auth.exception;

public class TokenExpiredException extends Exception{
    public TokenExpiredException() {
        super("Token expired!");
    }
}
