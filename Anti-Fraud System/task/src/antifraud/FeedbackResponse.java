package antifraud;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedbackResponse {
    private long transactionId;
    private long amount;
    private String ip;
    private String number;
    private String region;
    private String date;
    private String result;
    private String feedback;

    static FeedbackResponse fromTransaction(Transaction tx) {
        return new FeedbackResponse(
        tx.getId(),
        tx.getAmount(),
        tx.getIp(),
        tx.getNumber(),
        tx.getRegion(),
        tx.getDate().toString(),
        tx.getResult(),
        tx.getResult()
        );
    }
}
