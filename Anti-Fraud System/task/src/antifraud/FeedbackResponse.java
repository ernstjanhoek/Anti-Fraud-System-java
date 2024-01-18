package antifraud;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FeedbackResponse {
    private long transactionId;
    private long amount;
    private String ip;
    private String number;
    private String region;
    // @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    private String result;
    private String feedback;

    static FeedbackResponse fromTransaction(Transaction tx) {
        return new FeedbackResponse(
        tx.getId(),
        tx.getAmount(),
        tx.getIp(),
        tx.getNumber(),
        tx.getRegion(),
        tx.getDate(),
        tx.getResult(),
        tx.getFeedback()
        );
    }
}
