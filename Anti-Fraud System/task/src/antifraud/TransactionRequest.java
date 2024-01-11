package antifraud;

import jakarta.validation.constraints.Min;

public class TransactionRequest {
    @Min(1)
    private long amount;
    public long getAmount() {
        return amount;
    }
    public void setAmount(long amount) {
        this.amount = amount;
    }
}