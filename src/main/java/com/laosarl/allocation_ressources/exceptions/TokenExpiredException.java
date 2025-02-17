package com.laosarl.allocation_ressources.exceptions;

public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException(String message){
        super(message);
    }
}
