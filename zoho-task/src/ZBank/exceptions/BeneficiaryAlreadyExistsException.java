package ZBank.exceptions;

import ZBank.Messages;

public class BeneficiaryAlreadyExistsException extends Exception{
    public BeneficiaryAlreadyExistsException(){
        super(Messages.BENEFICIARY_ALREADY_EXISTS);
    }
}
