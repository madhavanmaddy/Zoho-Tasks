package ZBank;

import ZBank.exceptions.*;
import java.util.*;
public class Account {
    private final String accountNumber;
    private final String ckycNumber;
    private final String ifscCode;
    private final String customerID;
    private String passwordHash;
    private int balance;
    private final List<Beneficiary> beneficiaries= new ArrayList<>();
    public Account(String _ckycNumber, String password){
        Random random = new Random();
        this.ckycNumber = _ckycNumber;
        this.accountNumber = Integer.toString(random.nextInt(15));
        this.ifscCode = "ZBNK0000123";
        this.customerID = Integer.toString(random.nextInt(9));
        this.passwordHash = Bank.generateHash(password);
        this.balance = 0;
    }
    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", ckycNumber='" + ckycNumber + '\'' +
                ", ifscCode='" + ifscCode + '\'' +
                ", customerID='" + customerID + '\'' +
                ", balance=" + balance +
                '}';
    }
    public boolean compareCkycNumber(String _ckycNumber){
        return Objects.equals(this.ckycNumber, _ckycNumber);
    }
    public void changePassword(String newPasswordHash){
        this.passwordHash = newPasswordHash;
    }
    public String getAccountNumber(){
        return this.accountNumber;
    }
    public String getIfscCode(){
        return this.ifscCode;
    }
    public String getCustomerID(){
        return this.customerID;
    }
    public boolean compareCustomerID(String _customerID){
        return Objects.equals(this.customerID, _customerID);
    }
    public boolean comparePasswordHash(String givenHash){
        return Objects.equals(this.passwordHash, givenHash);
    }
    public void addBeneficiary(Beneficiary newBeneficiary) throws BeneficiaryAlreadyExistsException {
            for(Beneficiary beneficiary : this.beneficiaries){
                if(beneficiary.getAccountNumber().equals(newBeneficiary.getAccountNumber())){
                    throw new BeneficiaryAlreadyExistsException();
                }
            }
            beneficiaries.add(newBeneficiary);

    }
    public void viewBeneficiaries(){
        for(Beneficiary beneficiary: beneficiaries){
            System.out.println(beneficiary.toString());
        }
    }
    public void removeBeneficiary(String accountNumber) throws BeneficiaryNotFoundException {
        for(Beneficiary beneficiary : beneficiaries) {
            if (beneficiary.getAccountNumber().equals(accountNumber)) {
                beneficiaries.remove(beneficiary);
                return;
            }
        }
        throw new BeneficiaryNotFoundException();
    }
    public void withdraw(int amount)throws InsufficientBalanceException {
        if(balance<amount){
            throw new InsufficientBalanceException();
        }
        balance = balance - amount;
        Transaction transaction = new Transaction(this.accountNumber,null,amount,"Cash Withdrawl",TransactionType.WITHDRAW);
        Bank.logTransaction(transaction);
    }
    public void deposit(int amount){
        balance = balance + amount;
        Transaction transaction = new Transaction(null,this.accountNumber,amount,"Cash Deposit",TransactionType.DEPOSIT);
        Bank.logTransaction(transaction);
    }
    public void transfer(String accountNumber, int amount) throws BeneficiaryNotFoundException, InsufficientBalanceException, AccountNotFoundException{
        if(this.balance < amount){
            throw new InsufficientBalanceException();
        }
            for(Beneficiary beneficiary : this.beneficiaries){
                if(beneficiary.getAccountNumber().equals(accountNumber)){
                    Bank.deposit(accountNumber,amount);
                    balance = balance - amount;
                    Transaction transaction = new Transaction(this.accountNumber,accountNumber,amount,"Bank Transfer",TransactionType.TRANSFER);
                    Bank.logTransaction(transaction);
                    return;
                }
            throw new BeneficiaryNotFoundException();
        }

    }
}
