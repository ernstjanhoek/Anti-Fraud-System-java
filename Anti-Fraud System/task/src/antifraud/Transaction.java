package antifraud;


import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;

@Data
@RequiredArgsConstructor
public class Transaction {
    @NonNull
    private Long amount;
    @NonNull
    private Long allowedLimit;
    @NonNull
    private Long manualLimit;
    private ArrayList<String> infoStringArray = new ArrayList<>();
    public void appendInfo(String value) {
        infoStringArray.add(value);
    }
    public String buildInfoString() {
        if (infoStringArray.isEmpty() ) {
            return "none";
        } else {
           Collections.sort(infoStringArray);
           return String.join(", ", infoStringArray);
        }

    }
    public enum TransactionProcess {
        ALLOWED, PROHIBITED, MANUAL_PROCESSING
    }
}
