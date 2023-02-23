package com.bank.exceptions;

import com.bank.models.Strings;

public class InputException extends Exception{
    private final int errorCode;
    public int getErrorCode(){
        return this.errorCode;
    }
    public InputException(int _errorCode, String _message){
        super(_message);
        this.errorCode = _errorCode;
    }
    public InputException(){
        super(Strings.INVALID_REQUEST);
        this.errorCode = 400;
    }
}
