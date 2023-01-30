package ZBank.exceptions;

public class TransactionException extends Exception{
    private final int errorCode;
    public int getErrorCode(){
        return this.errorCode;
    }
    public TransactionException(int _errorCode, String message){
        super(message);
        this.errorCode = _errorCode;
    }
}

/*
404 - Transaction Not Found or No Transaction Found
 */