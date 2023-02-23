package com.bank.models;
public class Beneficiary {
    private final int accountNumber;
    private final String ifscCode;
    private final String nickName;
    public Beneficiary(int _accountNumber, String _ifscCode, String _nickName){
        this.accountNumber = _accountNumber;
        this.ifscCode = _ifscCode;
        this.nickName = _nickName;
    }
    @Override
    public String toString() {
        return "Beneficiary{" +
                "accountNumber='" + accountNumber + '\'' +
                ", ifscCode='" + ifscCode + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
    public int getAccountNumber(){
        return this.accountNumber;
    }
    public String getIfscCode(){
        return this.ifscCode;
    }
    public String getNickName(){
        return this.nickName;
    }
}
