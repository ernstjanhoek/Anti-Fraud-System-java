package antifraud;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transaction {
    private long amount;
    private long allowedLimit;
    private long manualLimit;
    public TransactionProcess validateInput() {
        if (this.amount <= this.allowedLimit) {
            return TransactionProcess.ALLOWED;
        } else if (this.amount <= this.manualLimit) {
            return TransactionProcess.MANUAL_PROCESSING;
        } else {
            return TransactionProcess.PROHIBITED;
        }
    }
    public enum TransactionProcess {
        ALLOWED, PROHIBITED, MANUAL_PROCESSING
    }
}
