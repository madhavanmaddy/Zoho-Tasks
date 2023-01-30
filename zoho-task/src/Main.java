import ZBank.*;
import ZBank.exceptions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    private static void getDetailsAndCreateAccount(){
        scanner.nextLine();
        System.out.println(Messages.ENTER_CKYC);
        String ckycNumber = scanner.nextLine();
        System.out.println(Messages.ENTER_PASSWORD);
        String password = scanner.nextLine();
        getDetailsAndCreateAccount(ckycNumber,password);
    }
    private static void getDetailsAndCreateAccount(String ckycNumber,String password){
        Account newAccount = new Account(ckycNumber,password);
        try {
            Bank.createAccount(newAccount);
            System.out.println(Messages.ACCOUNT_CREATION_SUCCESS);
            System.out.println(Messages.YOUR_CUSTOMER_ID+newAccount.getCustomerID());
            System.out.println(Messages.YOUR_ACCOUNT_NUMBER+newAccount.getAccountNumber());
        } catch (AccountException e) {
            if(e.getErrorCode() == 409) {
                getDetailsAndCreateAccount(ckycNumber,password);
            }
        }
    }
    private static void getDetailsAndDepositCash(){
        scanner.nextLine();
        System.out.println(Messages.ENTER_DESTINATION_ACCOUNT_NUMBER);
        String destinationAccountNumber = scanner.nextLine();
        getDetailsAndDepositCash(destinationAccountNumber);
    }
    private static void getDetailsAndDepositCash(String accountNumber){
        System.out.println(Messages.ENTER_DEPOSIT_AMOUNT);
        int amount = scanner.nextInt();
        try {
            Bank.deposit(accountNumber,amount);
            System.out.println(Messages.DEPOSIT_SUCCESS);
        } catch (AccountException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndLogin(){
        scanner.nextLine();
        System.out.println(Messages.ENTER_CUSTOMER_ID);
        String customerID = scanner.nextLine();
        System.out.println(Messages.ENTER_PASSWORD);
        String password = scanner.nextLine();
        String passwordHash = Bank.generateHash(password);
        try {
            Account account = Bank.login(customerID,passwordHash);
            System.out.println(Messages.LOGIN_SUCCESS);
            goToUserMenu(account);
        } catch (AccountException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndChangePassword(Account account){
        scanner.nextLine();
        System.out.println(Messages.ENTER_NEW_PASSWORD);
        String password = scanner.nextLine();
        String newPasswordHash = Bank.generateHash(password);
        account.changePassword(newPasswordHash);
        System.out.println(Messages.PASSWORD_CHANGE_SUCCESS);
    }
    private static void getDetailsAndChangePassword(){
        scanner.nextLine();
        System.out.println(Messages.ENTER_CUSTOMER_ID);
        String customerID = scanner.nextLine();
        System.out.println(Messages.ENTER_CKYC);
        String ckycNumber = scanner.nextLine();
        try {
            Account account = Bank.getAccountByCustomerID(customerID);
            if(account.compareCkycNumber(ckycNumber)){
                getDetailsAndChangePassword(account);
            }else{
                System.out.println(Messages.CKYC_MISMATCH);
            }

        } catch (AccountException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndWithdrawCash(Account account){
        System.out.println(Messages.ENTER_WITHDRAWL_AMOUNT);
        int amount = scanner.nextInt();
        try {
            account.withdraw(amount);
            System.out.println(Messages.WITHDRAWL_SUCCESS);
        } catch (AccountException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndBankTransfer(Account account){
        scanner.nextLine();
        System.out.println(Messages.ENTER_DESTINATION_ACCOUNT_NUMBER);
        String destinationAccountNumber = scanner.nextLine();
        System.out.println(Messages.ENTER_DESTINATION_IFSC_CODE);
        String destinationIfscCode = scanner.nextLine();
        System.out.println(Messages.ENTER_TRANSFER_AMOUNT);
        int amount = scanner.nextInt();
        getDetailsAndBankTransfer(account,destinationAccountNumber,destinationIfscCode,amount);
    }
    private static void getDetailsAndBankTransfer(Account account,String destinationAccountNumber,String destinationIfscCode,int amount){
        try {
            account.transfer(destinationAccountNumber,amount);
            System.out.println(Messages.TRANSFER_SUCCESS);
        } catch (BeneficiaryException e) {
            if(e.getErrorCode() == 404){
                System.out.println(Messages.BENEFICIARY_NOT_FOUND);
                System.out.println(Messages.ADD_BENEFICIARY_NOW);
                System.out.println(Messages.YES_OR_NO);
                scanner.nextLine();
                String choice = scanner.nextLine();
                if(Objects.equals(choice, "y") | Objects.equals(choice, "Y")){
                    getDetailsAndAddBeneficiary(account,destinationAccountNumber,destinationIfscCode);
                    System.out.println(Messages.CONTINUE_TRANSACTION_NOW);
                    System.out.println(Messages.YES_OR_NO);
                    choice = scanner.nextLine();
                    if(Objects.equals(choice, "y") | Objects.equals(choice, "Y")){
                        getDetailsAndBankTransfer(account,destinationAccountNumber,destinationIfscCode,amount);
                    }
                }
            }
        } catch (AccountException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndAddBeneficiary(Account account){
        scanner.nextLine();
        System.out.println(Messages.ENTER_DESTINATION_ACCOUNT_NUMBER);
        String destinationAccountNumber = scanner.nextLine();
        System.out.println(Messages.ENTER_DESTINATION_IFSC_CODE);
        String destinationIfscCode = scanner.nextLine();
        getDetailsAndAddBeneficiary(account,destinationAccountNumber,destinationIfscCode);
    }
    private static void getDetailsAndAddBeneficiary(Account account, String destinationAccountNumber,String destinationIfscCode){
        Beneficiary newBeneficiary = new Beneficiary(destinationAccountNumber,destinationIfscCode);
        try {
            account.addBeneficiary(newBeneficiary);
            System.out.println(Messages.ADD_BENEFICIARY_SUCCESS);
        } catch (BeneficiaryException e) {
            System.out.println(e.getMessage());
        }

    }
    private static void getDetailsAndRemoveBeneficiary(Account account){
        scanner.nextLine();
        System.out.println(Messages.ENTER_DESTINATION_ACCOUNT_NUMBER);
        String destinationAccountNumber = scanner.nextLine();
        try {
            account.removeBeneficiary(destinationAccountNumber);
            System.out.println(Messages.REMOVE_BENEFICIARY_SUCCESS);
        } catch (BeneficiaryException e) {
            System.out.println(e.getMessage());
        }

    }
    private static void viewAllAccounts(){
        try {
            Bank.viewAllAccounts();
        } catch (AccountException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void viewAllTransactions(){
        try {
            Bank.viewAllTransactions();
        } catch (TransactionException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void viewAllBeneficiaries(Account account){
        try {
            account.viewBeneficiaries();
        } catch (BeneficiaryException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void showWelcomeMessage(){
        System.out.println(Messages.WELCOME);
        System.out.println(Messages.CHOOSE_FROM_FOLLOWING);
        System.out.println("1. "+Messages.CREATE_ACCOUNT);
        System.out.println("2. "+Messages.DEPOSIT_CASH);
        System.out.println("3. "+Messages.LOGIN);
        System.out.println("4. "+Messages.FORGOT_PASSWORD);
        System.out.println("5. "+Messages.VIEW_ALL_ACCOUNTS);
        System.out.println("6. "+Messages.VIEW_ALL_TRANSACTIONS);
        System.out.println("0. "+Messages.QUIT);
        System.out.println(Messages.ENTER_CHOICE);
    }
    private static void showUserOptions(){
        System.out.println("1. "+Messages.VIEW_ACCOUNT_DETAILS);
        System.out.println("2. "+Messages.CHANGE_PASSWORD);
        System.out.println("3. "+Messages.DEPOSIT_CASH);
        System.out.println("4. "+Messages.WITHDRAW_CASH);
        System.out.println("5. "+Messages.BANK_TRANSFER);
        System.out.println("6. "+Messages.VIEW_BENEFICIARIES);
        System.out.println("7. "+Messages.ADD_BENEFICIARY);
        System.out.println("8. "+Messages.REMOVE_BENEFICIARY);
        System.out.println("9. "+Messages.LOGOUT);
        System.out.println(Messages.ENTER_CHOICE);
    }
    private static void goToUserMenu(Account account){
        int choice;
        do{
            showUserOptions();
            choice = scanner.nextInt();
            if(choice < 1 | choice > 9){
                System.out.println(Messages.INVALID_INPUT);
            }else{
                switch (choice){
                    case 1:
                        System.out.println(account.toString());
                        break;
                    case 2:
                        getDetailsAndChangePassword(account);
                        break;
                    case 3:
                        getDetailsAndDepositCash(account.getAccountNumber());
                        break;
                    case 4:
                        getDetailsAndWithdrawCash(account);
                        break;
                    case 5:
                        getDetailsAndBankTransfer(account);
                        break;
                    case 6:
                        viewAllBeneficiaries(account);
                        break;
                    case 7:
                        getDetailsAndAddBeneficiary(account);
                        break;
                    case 8:
                        getDetailsAndRemoveBeneficiary(account);
                        break;
                    case 9:
                        System.out.println(Messages.LOGGING_OUT);
                        break;
                    default:
                        System.out.println(Messages.THANK_YOU);
                        System.out.println(Messages.KEEP_BANKING);
                        break;
                }
            }
        }while(choice!=9);
    }
    private static void goToGeneralMenu(){
        int choice;
        do{
            showWelcomeMessage();
            choice = scanner.nextInt();
            if(choice < 0 | choice > 6){
                System.out.println(Messages.INVALID_INPUT);
            }
            else{
                switch (choice){
                    case 1:
                        getDetailsAndCreateAccount();
                        break;
                    case 2:
                        getDetailsAndDepositCash();
                        break;
                    case 3:
                        getDetailsAndLogin();
                        break;
                    case 4:
                        getDetailsAndChangePassword();
                        break;
                    case 5:
                        viewAllAccounts();
                        break;
                    case 6:
                        viewAllTransactions();
                        break;
                    default:
                        System.out.println(Messages.THANK_YOU);
                        System.out.println(Messages.KEEP_BANKING);
                        break;
                }
            }
        }while (choice != 0);
    }
    private static void connectToDataBase(){
        try {
           String MYSQL_USER =  System.getenv("MYSQLUSER");
           String MYSQL_PASSWORD = System.getenv("MYSQLPASS");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bank",MYSQL_USER,MYSQL_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        connectToDataBase();
       goToGeneralMenu();
    }
}