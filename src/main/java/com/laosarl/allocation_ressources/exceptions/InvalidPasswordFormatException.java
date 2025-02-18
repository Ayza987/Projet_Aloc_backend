package com.laosarl.allocation_ressources.exceptions;

public class InvalidPasswordFormatException extends RuntimeException{
    public InvalidPasswordFormatException(String message){
        super(message);
    }
}
