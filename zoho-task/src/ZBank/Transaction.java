package ZBank;

public class Transaction {
    private String dateTime;
    private int transactionId;
    private int fromAccount;
    private int toAccount;
    private final int amount;
    private final String remarks;
    private final TransactionType transactionType;
    public Transaction(String _dateTime,int _transactionId,int _fromAccount, int _toAccount, int _amount, String _remarks, TransactionType _transactionType){
        this.dateTime = _dateTime;
        this.transactionId = _transactionId;
        this.fromAccount = _fromAccount;
        this.toAccount = _toAccount;
        this.amount = _amount;
        this.remarks = _remarks;
        this.transactionType = _transactionType;
    }
    Transaction(int _amount,String _remarks,TransactionType _transactionType){
        this.amount = _amount;
        this.remarks = _remarks;
        this.transactionType = _transactionType;
    }
    public int getFromAccount(){
        return this.fromAccount;
    }
    public void setFromAccount(int _fromAccount){
        this.fromAccount = _fromAccount;
    }
    public void setToAccount(int _toAccount){
        this.toAccount = _toAccount;
    }
    public int getToAccount(){
        return this.toAccount;
    }

    public int getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getRemarks() {
        return remarks;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "dateTime='" + dateTime + '\'' +
                ", transactionId=" + transactionId +
                ", fromAccount=" + fromAccount +
                ", toAccount=" + toAccount +
                ", amount=" + amount +
                ", remarks='" + remarks + '\'' +
                ", transactionType=" + transactionType +
                '}';
    }
}
