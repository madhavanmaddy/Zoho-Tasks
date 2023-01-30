package ZBank;

import ZBank.exceptions.*;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    public static int getTransactionID(){
        return transactions.size() + 1 ;
    }
    public static void createAccount(Account newAccount) throws AccountException{
        for(Account account: accounts){
            if(account.getAccountNumber().equals(newAccount.getAccountNumber())){
                throw new AccountException(409,Messages.ACCOUNT_ALREADY_EXISTS);
            }
        }
        accounts.add(newAccount);
    }
    public static Account login(String customerID,String passwordHash) throws AccountException {
        Account account = getAccountByCustomerID(customerID);
        if(account.comparePasswordHash(passwordHash)){
            return account;
        }else{
            throw new AccountException(401,Messages.PASSWORD_MISMATCH);
        }
    }
    public static String generateHash(String password){
        return password;
    }
    public static void deposit(String accountNumber,int amount) throws AccountException{
           Account account = Bank.getAccountByAccountNumber(accountNumber);
           account.deposit(amount);

    }

    private static final List<Transaction> transactions = new ArrayList<>();
    private static final List<Account> accounts = new ArrayList<>();
    private static Account getAccountByAccountNumber(String accountNumber) throws AccountException{

        for(Account account : accounts){
            if(account.getAccountNumber().equals(accountNumber)){
                return account;
            }
        }
        throw new AccountException(404,Messages.ACCOUNT_NOT_FOUND);
    }
    public static Account getAccountByCustomerID(String customerID) throws AccountException {
        for(Account account : accounts){
            if(account.compareCustomerID(customerID)){
                return account;
            }
        }
        throw new AccountException(404,Messages.ACCOUNT_NOT_FOUND);
    }
    public static void logTransaction(Transaction transaction){
        transactions.add(transaction);
    }
    public static void viewAllAccounts()throws AccountException {
        if(accounts.isEmpty()){
            throw new AccountException(404,Messages.NO_ACCOUNT_FOUND);
        }
        for(Account account: accounts){
            System.out.println(account.toString());
        }
    }
    public static void viewAllTransactions() throws TransactionException {
        if(transactions.isEmpty()){
            throw new TransactionException(404,Messages.NO_TRANSACTION_FOUND);
        }
    }
}
