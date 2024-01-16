package antifraud;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Transaction {
    @NonNull
    private Long amount;
    @NonNull
    private Long allowedLimit;
    @NonNull
    private Long manualLimit;
    private String infoString = "none";
    public TransactionProcess validateInput() {
        if (this.amount <= this.allowedLimit) {
            return TransactionProcess.ALLOWED;
        } else if (this.amount <= this.manualLimit) {
            this.infoString = "amount";
            return TransactionProcess.MANUAL_PROCESSING;
        } else {
            this.infoString = "amount";
            return TransactionProcess.PROHIBITED;
        }
    }
    // public void infoStringBuilder() {
    //     if (infoString.isEmpty()) {
    //         this.infoString = "none";
    //     }
    // }
    public enum TransactionProcess {
        ALLOWED, PROHIBITED, MANUAL_PROCESSING
    }
}
