package com.bank.models;

import com.bank.exceptions.AccountException;
import com.bank.exceptions.TransactionException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bank {
    public static int getNewCustomerID(String ckycNumber) throws SQLException {
        Connection connection =  Database.connection;
        PreparedStatement statement = connection.prepareStatement("select * from customers where ckycNumber=?");
        statement.setString(1,ckycNumber);
        ResultSet searchResult = statement.executeQuery();
        if(searchResult.next()){
            return searchResult.getInt(1);
        }else{
            statement = connection.prepareStatement("insert into customers (ckycNumber) values (?)");
            statement.setString(1,ckycNumber);
            statement.execute();
            return getNewCustomerID(ckycNumber);
        }
    }
    private static int getNewAccountNumber(Account account)throws AccountException,SQLException{
            Connection connection =  Database.connection;
            PreparedStatement statement = connection.prepareStatement("insert into accounts (ifscCode,customerId,passwordHash) values (?,?,?)",Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, account.getIfscCode());
            statement.setInt(2,account.getCustomerID());
            statement.setString(3, account.getPasswordHash());
            int affectedRows = statement.executeUpdate();
            if(affectedRows == 0){
                throw new AccountException(500, Strings.ACCOUNT_CREATION_FAILURE);
            }
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if(generatedKeys.next()){
             return generatedKeys.getInt(1);
            }
            return getNewAccountNumber(account);
    }
    public static void createAccount (Account newAccount) throws AccountException , SQLException{
        int customerId = getNewCustomerID(newAccount.getCkycNumber());
        newAccount.setCustomerID(customerId);
        int accountNumber = getNewAccountNumber(newAccount);
        newAccount.setAccountNumber(accountNumber);
    }
    public static Account login(int accountNumber,String passwordHash) throws AccountException, SQLException {
        Account account = getAccountByAccountNumber(accountNumber);
        if(account.comparePasswordHash(passwordHash)){
            return account;
        }else{
            throw new AccountException(401, Strings.PASSWORD_MISMATCH);
        }
    }
    public static String generateHash(String password){
        return password;
    }
    public static int deposit(int accountNumber, int amount) throws AccountException, SQLException, TransactionException {
           Account account = Bank.getAccountByAccountNumber(accountNumber);
           return account.deposit(amount);
    }
    public static Account getAccountByAccountNumber(int accountNumber) throws AccountException,SQLException{
        Connection connection =  Database.connection;
        PreparedStatement statement = connection.prepareStatement("select * from accountsView where accountNumber=?");
        statement.setInt(1,accountNumber);
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()){
            return new Account(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getInt(4),resultSet.getString(5),resultSet.getInt(6));
        }
        throw new AccountException(404, Strings.ACCOUNT_NOT_FOUND);
    }
    public static List<Account> getAccountsByCustomerID(int customerID) throws SQLException {
        Connection connection =  Database.connection;
        PreparedStatement statement = connection.prepareStatement("select * from accountsView where customerId=?");
        statement.setInt(1,customerID);
        ResultSet resultSet = statement.executeQuery();
        List<Account> accounts = new ArrayList<>();
        while(resultSet.next()){
            accounts.add(new Account(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getInt(4),resultSet.getString(5),resultSet.getInt(6)));

        }
        return accounts;
    }
    public static int logTransaction(Transaction transaction)throws SQLException,TransactionException{
        Connection connection =  Database.connection;
        PreparedStatement statement = connection.prepareStatement("insert into transactions (dateTime,fromAccount,toAccount,amount,remarks,transactionType) values (?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, LocalDateTime.now().toString());
        statement.setInt(4,transaction.getAmount());
        statement.setString(5,transaction.getRemarks());
        statement.setInt(6, transaction.getTransactionType().ordinal());
        if(transaction.getTransactionType() == TransactionType.WITHDRAW){
            statement.setInt(2,transaction.getFromAccount());
            statement.setObject(3,null);
        }
        if (transaction.getTransactionType() == TransactionType.DEPOSIT){
            statement.setObject(2,null);
            statement.setInt(3,transaction.getToAccount());
        }
        if(transaction.getTransactionType() == TransactionType.TRANSFER){
            statement.setInt(2,transaction.getFromAccount());
            statement.setInt(3,transaction.getToAccount());
        }
        int affectedRows = statement.executeUpdate();
        if(affectedRows !=1){
            throw new TransactionException(500, Strings.TRANSACTION_LOG_FAILURE);
        }
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if(generatedKeys.next()){
            return generatedKeys.getInt(1);
        }
        return 0;
    }
    public static List<Account> viewAllAccounts()throws AccountException,SQLException {
        Connection connection =  Database.connection;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from accountsView");
        List<Account> accountsQueryResult = new ArrayList<>();
        while (resultSet.next()){
            accountsQueryResult.add(new Account(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),resultSet.getInt(4),resultSet.getString(5),resultSet.getInt(6)));
        }
        if(accountsQueryResult.isEmpty()){
            throw new AccountException(404, Strings.NO_ACCOUNT_FOUND);
        }
        return accountsQueryResult;
    }
    public static List<Transaction> viewAllTransactions() throws TransactionException,SQLException {
        Connection connection =  Database.connection;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from transactions");
        List<Transaction> transactionQueryResult = new ArrayList<>();
        while (resultSet.next()){
            transactionQueryResult.add(new Transaction(resultSet.getString(1),resultSet.getInt(2),resultSet.getInt(3),resultSet.getInt(4),resultSet.getInt(5),resultSet.getString(6),TransactionType.values()[resultSet.getInt(7)]));
        }
        if(transactionQueryResult.isEmpty()){
            throw new TransactionException(404, Strings.NO_TRANSACTION_FOUND);
        }
        return transactionQueryResult;
    }
    public static void closeAccount(int accountNumber) throws SQLException, AccountException {
        Connection connection =  Database.connection;
        PreparedStatement statement = connection.prepareStatement("update accounts set deleted=true where accountNumber=?");
        statement.setInt(1,accountNumber);
        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0){
            throw new AccountException(500, Strings.ACCOUNT_DELETION_FAILURE);
        }
        throw new AccountException(200,Strings.ACCOUNT_DELETION_SUCCESS);
    }
}
