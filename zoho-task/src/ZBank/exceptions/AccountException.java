package ZBank.exceptions;

public class AccountException extends Exception{
    private final int errorCode;
    public int getErrorCode(){
        return this.errorCode;
    }
    public AccountException(int _errorCode, String message){
        super(message);
        this.errorCode = _errorCode;
    }
}

/*
400 - Insufficient Balance
401 - Password Mismatch
404 - Account Not Found
409 - Account Already Exists
 */