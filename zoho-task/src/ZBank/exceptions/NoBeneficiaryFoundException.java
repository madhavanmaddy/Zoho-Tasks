package ZBank.exceptions;

import ZBank.Messages;

public class NoBeneficiaryFoundException extends Exception{
    public NoBeneficiaryFoundException(){
        super(Messages.NO_BENEFICIARY_FOUND);
    }
}
