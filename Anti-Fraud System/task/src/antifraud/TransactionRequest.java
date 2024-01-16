package antifraud;

import jakarta.validation.constraints.Min;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
public class TransactionRequest {
    @Min(1)
    private long amount;
    private String ip;
    private String number;
    private String region;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime date;
}