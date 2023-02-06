package com.bank.exceptions;

public class BeneficiaryException extends Exception{
    private final int errorCode;
    public int getErrorCode(){
        return this.errorCode;
    }
    public BeneficiaryException(int _errorCode, String message){
        super(message);
        this.errorCode = _errorCode;
    }
}

/*
404 - Beneficiary Not Found
409 - Beneficiary Already Exists
 */
