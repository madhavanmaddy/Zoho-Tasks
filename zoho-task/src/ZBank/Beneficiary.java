package ZBank;
public class Beneficiary {
    private String accountNumber;
    private String ifscCode;
    private String nickName;
    public Beneficiary(String _accountNumber, String _ifscCode){
        this.accountNumber = _accountNumber;
        this.ifscCode = _ifscCode;
    }
    Beneficiary(String _accountNumber,String _ifscCode,String _nickName){
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
    public String getAccountNumber(){
        return this.accountNumber;
    }
    public String getIfscCode(){
        return this.ifscCode;
    }
    public String getNickName(){
        return this.nickName;
    }
}
