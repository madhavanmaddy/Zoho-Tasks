package ZBank.exceptions;

import ZBank.Messages;

public class BeneficiaryNotFoundException extends Exception{
    public BeneficiaryNotFoundException(){
        super(Messages.BENEFICIARY_NOT_FOUND);
    }
}
