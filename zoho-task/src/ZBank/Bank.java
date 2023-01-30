package ZBank;

import ZBank.exceptions.*;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    public static int getTransactionID(){
        return transactions.size() + 1 ;
    }
    public static void createAccount(Account newAccount) throws AccountAlreadyExistsException {
        for(Account account: accounts){
            if(account.getAccountNumber().equals(newAccount.getAccountNumber())){
                throw new AccountAlreadyExistsException();
            }
        }
        accounts.add(newAccount);
    }
    public static Account login(String customerID,String passwordHash) throws AccountNotFoundException, PasswordMismatchException {
        Account account = getAccountByCustomerID(customerID);
        if(account.comparePasswordHash(passwordHash)){
            return account;
        }else{
            throw new PasswordMismatchException();
        }
    }
    public static String generateHash(String password){
        return password;
    }
    public static void deposit(String accountNumber,int amount) throws AccountNotFoundException{
           Account account = Bank.getAccountByAccountNumber(accountNumber);
           account.deposit(amount);

    }

    private static final List<Transaction> transactions = new ArrayList<>();
    private static final List<Account> accounts = new ArrayList<>();
    private static Account getAccountByAccountNumber(String accountNumber) throws AccountNotFoundException{

        for(Account account : accounts){
            if(account.getAccountNumber().equals(accountNumber)){
                return account;
            }
        }
        throw new AccountNotFoundException();
    }
    public static Account getAccountByCustomerID(String customerID) throws AccountNotFoundException {
        for(Account account : accounts){
            if(account.compareCustomerID(customerID)){
                return account;
            }
        }
        throw new AccountNotFoundException();
    }
    public static void logTransaction(Transaction transaction){
        transactions.add(transaction);
    }
    public static void viewAllAccounts()throws NoAccountFoundException {
        if(accounts.isEmpty()){
            throw new NoAccountFoundException();
        }
        for(Account account: accounts){
            System.out.println(account.toString());
        }
    }
    public static void viewAllTransactions() throws NoTransactionFoundException {
        if(transactions.isEmpty()){
            throw new NoTransactionFoundException();
        }
    }
}
