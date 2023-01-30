package ZBank.exceptions;

import ZBank.Messages;

public  class AccountNotFoundException extends Exception{
    public AccountNotFoundException(){
        super(Messages.ACCOUNT_NUMBER_NOT_FOUND);
    }
}
