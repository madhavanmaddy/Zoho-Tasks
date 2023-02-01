import ZBank.*;
import ZBank.exceptions.*;
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

    public static Account chooseAnAccount(List<Account> accounts){
        System.out.println("Following Accounts are found for your Customer ID: ");
        for (Account account: accounts){
            System.out.println(account);
        }
        System.out.println("Please Enter an Account Number to Continue");
        int accountNumber = scanner.nextInt();
        for(Account account: accounts){
            if(account.getAccountNumber() == accountNumber){
                return account;
            }
        }
        System.out.println(Messages.INVALID_INPUT);
        return chooseAnAccount(accounts);
    }
    private static void viewAccountDetails(int accountNumber){
        try {
          Account account = Bank.getAccountByAccountNumber(accountNumber);
          System.out.println(account);
        } catch (AccountException | SQLException e) {
            System.out.println(e.getMessage());
        }
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
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndDepositCash(){
        scanner.nextLine();
        System.out.println(Messages.ENTER_DESTINATION_ACCOUNT_NUMBER);
        int destinationAccountNumber = scanner.nextInt();
        getDetailsAndDepositCash(destinationAccountNumber);
    }
    private static void getDetailsAndDepositCash(int accountNumber){
        System.out.println(Messages.ENTER_DEPOSIT_AMOUNT);
        int amount = scanner.nextInt();
        try {
            Bank.deposit(accountNumber,amount);
            System.out.println(Messages.DEPOSIT_SUCCESS);
        } catch (AccountException | SQLException | TransactionException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndLogin(){
        System.out.println(Messages.ENTER_ACCOUNT_NUMBER);
        int accountNumber = scanner.nextInt();
        scanner.nextLine();
        System.out.println(Messages.ENTER_PASSWORD);
        String password = scanner.nextLine();
        String passwordHash = Bank.generateHash(password);
        try {
            Account account = Bank.login(accountNumber,passwordHash);
            System.out.println(Messages.LOGIN_SUCCESS);
            goToUserMenu(account);
        } catch (AccountException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndChangePassword(Account account){
        scanner.nextLine();
        System.out.println(Messages.ENTER_NEW_PASSWORD);
        String password = scanner.nextLine();
        String newPasswordHash = Bank.generateHash(password);
        try {
            account.changePassword(newPasswordHash);
        } catch (SQLException | TransactionException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndChangePassword(){
        scanner.nextLine();
        System.out.println(Messages.ENTER_CUSTOMER_ID);
        int customerID = scanner.nextInt();
        System.out.println(Messages.ENTER_CKYC);
        String ckycNumber = scanner.nextLine();
        try {
            List<Account> accounts = Bank.getAccountsByCustomerID(customerID);
            Account account = chooseAnAccount(accounts);
            if(account.compareCkycNumber(ckycNumber)){
                getDetailsAndChangePassword(account);
            }else{
                System.out.println(Messages.CKYC_MISMATCH);
            }

        } catch (AccountException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndWithdrawCash(Account account){
        System.out.println(Messages.ENTER_WITHDRAWL_AMOUNT);
        int amount = scanner.nextInt();
        try {
            account.withdraw(amount);
            System.out.println(Messages.WITHDRAWL_SUCCESS);
        } catch (AccountException | TransactionException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndBankTransfer(Account account){
        scanner.nextLine();
        System.out.println(Messages.ENTER_DESTINATION_ACCOUNT_NUMBER);
        int destinationAccountNumber = scanner.nextInt();
        System.out.println(Messages.ENTER_DESTINATION_IFSC_CODE);
        scanner.nextLine();
        String destinationIfscCode = scanner.nextLine();
        System.out.println(Messages.ENTER_TRANSFER_AMOUNT);
        int amount = scanner.nextInt();
        getDetailsAndBankTransfer(account,destinationAccountNumber,destinationIfscCode,amount);
    }
    private static void getDetailsAndBankTransfer(Account account,int destinationAccountNumber,String destinationIfscCode,int amount){
        try {
            account.transfer(destinationAccountNumber,amount);
        } catch (BeneficiaryException e) {
            if(e.getErrorCode() == 404){
                System.out.println(e.getMessage());
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
        } catch (AccountException | SQLException | TransactionException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void getDetailsAndAddBeneficiary(Account account){
        scanner.nextLine();
        System.out.println(Messages.ENTER_DESTINATION_ACCOUNT_NUMBER);
        int destinationAccountNumber = scanner.nextInt();
        System.out.println(Messages.ENTER_DESTINATION_IFSC_CODE);
        scanner.nextLine();
        String destinationIfscCode = scanner.nextLine();
        getDetailsAndAddBeneficiary(account,destinationAccountNumber,destinationIfscCode);
    }
    private static void getDetailsAndAddBeneficiary(Account account, int destinationAccountNumber,String destinationIfscCode){
        Beneficiary newBeneficiary = new Beneficiary(destinationAccountNumber,destinationIfscCode);
        try {
            account.addBeneficiary(newBeneficiary);
        } catch (BeneficiaryException | SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    private static void getDetailsAndRemoveBeneficiary(Account account){
        scanner.nextLine();
        System.out.println(Messages.ENTER_DESTINATION_ACCOUNT_NUMBER);
        int destinationAccountNumber = scanner.nextInt();
        try {
            account.removeBeneficiary(destinationAccountNumber);
        } catch (BeneficiaryException |SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    private static void viewAllAccounts(){
        try {
            Bank.viewAllAccounts();
        } catch (AccountException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void viewAllTransactions(){
        try {
            Bank.viewAllTransactions();
        } catch (TransactionException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void viewAllTransactionsOfAccount(Account account){
        try{
            account.viewAllTransactions();
        } catch (TransactionException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void viewAllBeneficiaries(Account account){
        try {
            account.viewBeneficiaries();
        } catch (BeneficiaryException | SQLException e) {
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
        System.out.println("9. "+Messages.VIEW_ALL_TRANSACTIONS);
        System.out.println("10. "+Messages.LOGOUT);
        System.out.println(Messages.ENTER_CHOICE);
    }
    private static void goToUserMenu(Account account){
        int choice;
        do{
            showUserOptions();
            choice = scanner.nextInt();
            if(choice < 1 | choice > 10){
                System.out.println(Messages.INVALID_INPUT);
            }else{
                switch (choice){
                    case 1:
                        viewAccountDetails(account.getAccountNumber());
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
                        viewAllTransactionsOfAccount(account);
                        break;
                    case 10:
                        System.out.println(Messages.LOGGING_OUT);
                        break;
                    default:
                        System.out.println(Messages.THANK_YOU);
                        System.out.println(Messages.KEEP_BANKING);
                        break;
                }
            }
        }while(choice!=10);
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
    public static void main(String[] args) throws SQLException {
        Database.getConnection();
        goToGeneralMenu();
    }
}