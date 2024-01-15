package antifraud;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuspiciousIPDTO {
    private long id;
    private String ip;
}
