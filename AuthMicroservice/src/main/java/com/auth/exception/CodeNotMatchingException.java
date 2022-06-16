package com.auth.exception;

public class CodeNotMatchingException extends RuntimeException {
    public CodeNotMatchingException() {
        super("Code is not matching");
    }
    
}
