package antifraud;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transaction {
    private long amount;
    private long allowedLimit;
    private long manualLimit;
    private String IPAddress;
    private String cardNumber;
    public TransactionProcess validateInput() {
        if (!this.isValidIP()) {
            return TransactionProcess.PROHIBITED;
        }
        if (!this.isValidCardNumber()) {
            return TransactionProcess.PROHIBITED;
        }
        if (this.amount <= this.allowedLimit) {
            return TransactionProcess.ALLOWED;
        } else if (this.amount <= this.manualLimit) {
            return TransactionProcess.MANUAL_PROCESSING;
        } else {
            return TransactionProcess.PROHIBITED;
        }
    }
    private boolean isValidIP() {
        return true;
    }
    private boolean isValidCardNumber() {
        return true;
    }
    public enum TransactionProcess {
        ALLOWED, PROHIBITED, MANUAL_PROCESSING
    }
}
