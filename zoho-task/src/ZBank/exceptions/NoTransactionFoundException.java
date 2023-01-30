package ZBank.exceptions;

import ZBank.Messages;

public class NoTransactionFoundException extends Exception{
    public NoTransactionFoundException(){
        super(Messages.NO_TRANSACTION_FOUND);
    }
}
