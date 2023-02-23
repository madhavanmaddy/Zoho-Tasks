package com.bank.models;

import com.bank.exceptions.AccountException;
import com.bank.exceptions.BeneficiaryException;
import com.bank.exceptions.InputException;
import com.bank.exceptions.TransactionException;
import com.google.gson.annotations.Expose;

import java.sql.*;
import java.util.*;
public class Account {
    @Expose()
    private  int accountNumber;
    @Expose()
    private final String ckycNumber;
    @Expose()
    private final String ifscCode;
    @Expose()
    private  int customerID;
    @Expose(serialize = false)
    private final String passwordHash;
    @Expose(serialize = false)
    private int balance;
    public Account(String _ckycNumber, String password)throws InputException {
        if(_ckycNumber.isEmpty() | password.isEmpty()){
            throw new InputException();
        }
        this.ckycNumber = _ckycNumber;
        this.ifscCode = "ZBNK0000123";
        this.passwordHash = Bank.generateHash(password);
    }
    public Account(int _accountNumber,String _ckycNumber,String _ifscCode,int _customerId,String _passwordHash,int _balance){
        this.accountNumber = _accountNumber;
        this.ckycNumber = _ckycNumber;
        this.customerID = _customerId;
        this.ifscCode = _ifscCode;
        this.passwordHash = _passwordHash;
        this.balance = _balance;
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
    public void changePassword(String newPasswordHash) throws SQLException, TransactionException {
        Connection connection = Database.connection;
        PreparedStatement statement = connection.prepareStatement("update accounts set passwordHash=? where accountNumber=?");
        statement.setString(1,newPasswordHash);
        statement.setInt(2,this.accountNumber);
        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0){
            throw new TransactionException(500, Strings.CHANGE_PASSWORD_FAILURE);
        }
    }
    public int getAccountNumber(){
        return this.accountNumber;
    }
    public int getCustomerID(){
        return this.customerID;
    }
    public String getCkycNumber(){return this.ckycNumber;}
    public String getIfscCode(){return this.ifscCode;}
    public void setAccountNumber(int _accountNumber){ this.accountNumber = _accountNumber;}
    public void setCustomerID(int _customerID){this.customerID = _customerID;}
    public String getPasswordHash(){return this.passwordHash;}
    public boolean comparePasswordHash(String givenHash){
        return Objects.equals(this.passwordHash, givenHash);
    }
    public void checkAndAddBeneficiary(Beneficiary newBeneficiary) throws BeneficiaryException,SQLException{
        try{
            getBeneficiary(newBeneficiary.getAccountNumber());
            throw new BeneficiaryException(409,Strings.BENEFICIARY_ALREADY_EXISTS);
        }catch (BeneficiaryException e){
            if(e.getErrorCode()==404){
                addBeneficiary(newBeneficiary);
            }
            if(e.getErrorCode() == 409){
                throw e;
            }
        }

    }
    private void addBeneficiary(Beneficiary newBeneficiary) throws BeneficiaryException,SQLException {
        Connection connection =  Database.connection;
        PreparedStatement statement = connection.prepareStatement("insert into beneficiaries (customerId,accountNumber,ifscCode,nickName) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1,this.customerID);
        statement.setInt(2,newBeneficiary.getAccountNumber());
        statement.setString(3, newBeneficiary.getIfscCode());
        statement.setString(4,newBeneficiary.getNickName());
        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0){
            throw new BeneficiaryException(500, Strings.ADD_BENEFICIARY_FAILURE);
        }
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if(generatedKeys.next()) {
            System.out.println(Strings.ADD_BENEFICIARY_SUCCESS);
        }
    }
    public List<Beneficiary> viewBeneficiaries() throws BeneficiaryException,SQLException{
        Connection connection =  Database.connection;
        PreparedStatement statement = connection.prepareStatement("select * from beneficiaries where customerId=?");
        statement.setInt(1,this.customerID);
        ResultSet resultSet = statement.executeQuery();
        List<Beneficiary> beneficiaryQueryResult = new ArrayList<>();
        while (resultSet.next()){
            beneficiaryQueryResult.add(new Beneficiary(resultSet.getInt(2),resultSet.getString(3),resultSet.getString(4)));
        }
        if(beneficiaryQueryResult.isEmpty()){
            throw new BeneficiaryException(404, Strings.NO_BENEFICIARY_FOUND);
        }
        return beneficiaryQueryResult;
    }
    public void removeBeneficiary(int accountNumber) throws BeneficiaryException,SQLException {
        Connection connection =  Database.connection;
        PreparedStatement statement = connection.prepareStatement("delete from beneficiaries where (customerId=?) and (accountNumber=?)", Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1,this.customerID);
        statement.setInt(2,accountNumber);
        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0){
            throw new BeneficiaryException(500, Strings.REMOVE_BENEFICIARY_FAILURE);
        }
        System.out.println(Strings.REMOVE_BENEFICIARY_SUCCESS);
    }
    public int withdraw(int amount)throws AccountException,SQLException,TransactionException {
        Account account = Bank.getAccountByAccountNumber(this.accountNumber);
        if(account.balance<amount){
            throw new AccountException(400, Strings.INSUFFICIENT_BALANCE);
        }
        Connection connection =  Database.connection;
        PreparedStatement statement = connection.prepareStatement("update accounts set balance=balance-? where accountNumber=?");
        statement.setInt(1,amount);
        statement.setInt(2,this.accountNumber);
        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0){
            throw new TransactionException(500, Strings.UPDATE_BALANCE_FAILURE);
        }
        Transaction transaction = new Transaction(amount,"Cash Withdrawl",TransactionType.WITHDRAW);
        transaction.setFromAccount(this.accountNumber);
        return Bank.logTransaction(transaction);
    }
    public int deposit(int amount) throws SQLException,TransactionException{
        Connection connection =  Database.connection;
        PreparedStatement statement = connection.prepareStatement("update accounts set balance=balance+? where accountNumber=?");
        statement.setInt(1,amount);
        statement.setInt(2,this.accountNumber);
        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0){
            throw new TransactionException(500, Strings.UPDATE_BALANCE_FAILURE);
        }
        Transaction transaction = new Transaction(amount,"Cash Deposit",TransactionType.DEPOSIT);
        transaction.setToAccount(this.accountNumber);
        return Bank.logTransaction(transaction);
    }
    public Beneficiary getBeneficiary(int accountNumber) throws SQLException,BeneficiaryException {
        Connection connection = Database.connection;
        PreparedStatement statement = connection.prepareStatement("select * from beneficiaries where (customerId=?) and (accountNumber=?)");
        statement.setInt(1,this.customerID);
        statement.setInt(2,accountNumber);
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.next()){
            return new Beneficiary(resultSet.getInt(2),resultSet.getString(3),resultSet.getString(4));
        }
        throw new BeneficiaryException(404, Strings.BENEFICIARY_NOT_FOUND);
    }
    public int transfer(int accountNumber, int amount) throws BeneficiaryException, AccountException, TransactionException, SQLException {
        Account account = Bank.getAccountByAccountNumber(this.accountNumber);
        if(account.balance < amount){
            throw new AccountException(400, Strings.INSUFFICIENT_BALANCE);
        }
        Connection connection = Database.connection;
        getBeneficiary(accountNumber);
        try{
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement("update accounts set balance=balance-? where accountNumber=?");
            statement.setInt(1,amount);
            statement.setInt(2,account.accountNumber);
            statement.executeUpdate();
            statement = connection.prepareStatement("update accounts set balance=balance+? where accountNumber=?");
            statement.setInt(1,amount);
            statement.setInt(2,accountNumber);
            statement.executeUpdate();
            connection.commit();
            Transaction transaction = new Transaction(amount,"Bank Transfer",TransactionType.TRANSFER);
            transaction.setFromAccount(account.accountNumber);
            transaction.setToAccount(accountNumber);
            return Bank.logTransaction(transaction);
        }catch (SQLException e) {
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }
        return 0;
    }
    public List<Transaction> viewAllTransactions() throws SQLException, TransactionException {
        Connection connection =  Database.connection;
        PreparedStatement statement = connection.prepareStatement("select * from transactions where fromAccount=? or toAccount=?");
        statement.setInt(1,this.accountNumber);
        statement.setInt(2,this.accountNumber);
        ResultSet resultSet = statement.executeQuery();
        List<Transaction> transactionQueryResult = new ArrayList<>();
        while (resultSet.next()){
            transactionQueryResult.add(new Transaction(resultSet.getString(1),resultSet.getInt(2),resultSet.getInt(3),resultSet.getInt(4),resultSet.getInt(5),resultSet.getString(6),TransactionType.values()[resultSet.getInt(7)]));
        }
        if(transactionQueryResult.isEmpty()){
            throw new TransactionException(404, Strings.NO_TRANSACTION_FOUND);
        }
        return transactionQueryResult;
    }
}
