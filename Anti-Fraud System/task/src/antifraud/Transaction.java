package antifraud;


import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private long id;
    @NonNull
    private Long amount;
    @NonNull
    private String ip;
    @NonNull
    private String number;
    @NonNull
    @Pattern(regexp = "EAP|ECA|HIC|LAC|MENA|SA|SSA")
    private String region;
    @NonNull
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime date;
    @NonNull
    @Transient
    private Long allowedLimit;
    @NonNull
    @Transient
    private Long manualLimit;
    @Transient
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
