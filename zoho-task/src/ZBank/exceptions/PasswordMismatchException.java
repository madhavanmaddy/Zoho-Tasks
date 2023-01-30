package ZBank.exceptions;

import ZBank.Messages;

public class PasswordMismatchException extends Exception{
    public PasswordMismatchException(){
        super(Messages.PASSWORD_MISMATCH);
    }
}
