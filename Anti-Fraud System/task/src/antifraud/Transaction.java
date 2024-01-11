package antifraud;

import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class Transaction {
    private long amount;
    private long allowedLimit;
    private long manualLimit;
    public void setAmount(long value) {
        this.amount = value;
    }
    public long getAmount() {
        return amount;
    }
    public void setAllowedLimit(long allowedLimit) {
        this.allowedLimit = allowedLimit;
    }
    public long getAllowedLimit() {
        return allowedLimit;
    }
    public long getManualLimit() {
        return manualLimit;
    }
    public void setManualLimit(long manualLimit) {
        this.manualLimit = manualLimit;
    }
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
        ALLOWED, PROHIBITED, MANUAL_PROCESSING;
    }
}
