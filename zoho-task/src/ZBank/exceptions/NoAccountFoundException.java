package ZBank.exceptions;

import ZBank.Messages;

public class NoAccountFoundException extends Exception{
    public NoAccountFoundException(){
        super(Messages.NO_ACCOUNT_FOUND);
    }
}
