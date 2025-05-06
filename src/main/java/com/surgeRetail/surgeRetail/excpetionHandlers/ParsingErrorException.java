package com.surgeRetail.surgeRetail.excpetionHandlers;

public class ParsingErrorException extends NumberFormatException {
    public ParsingErrorException(String message) {
        super(message);
    }
}
