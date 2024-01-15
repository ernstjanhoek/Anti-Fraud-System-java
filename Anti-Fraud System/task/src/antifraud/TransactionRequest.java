package antifraud;

import jakarta.validation.constraints.Min;

import lombok.Data;

@Data
public class TransactionRequest {
    @Min(1)
    private long amount;
}