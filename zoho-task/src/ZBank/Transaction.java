package ZBank;
import java.time.LocalDateTime;
enum TransactionType{
    DEPOSIT,
    WITHDRAW,
    TRANSFER
}
public class Transaction {
    private final LocalDateTime dateTime;
    private final int trasactionID;
    private final String fromAccount;
    private final String toAccount;
    private final int amount;
    private final String remarks;
    private final TransactionType transactionType;
    Transaction(String _fromAccount,String _toAccount,int _amount,String _remarks,TransactionType _transactionType){
        this.dateTime = LocalDateTime.now();
        this.trasactionID = Bank.getTransactionID();
        this.fromAccount = _fromAccount;
        this.toAccount = _toAccount;
        this.amount = _amount;
        this.remarks = _remarks;
        this.transactionType = _transactionType;
    }
    public String getFromAccount(){
        return this.fromAccount;
    }
    public String getToAccount(){
        return this.toAccount;
    }
    @Override
    public String toString() {
        return "Transaction{" +
                "dateTime=" + dateTime +
                ", trasactionID=" + trasactionID +
                ", fromAccount='" + fromAccount + '\'' +
                ", toAccount='" + toAccount + '\'' +
                ", amount=" + amount +
                ", remarks='" + remarks + '\'' +
                ", transactionType=" + transactionType +
                '}';
    }
}
