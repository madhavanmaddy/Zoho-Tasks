package ZBank.exceptions;

import ZBank.Messages;

public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(){
        super(Messages.INSUFFICIENT_BALANCE);
    }
}
