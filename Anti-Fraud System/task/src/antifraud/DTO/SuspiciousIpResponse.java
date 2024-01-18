package antifraud.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuspiciousIpResponse {
    private long id;
    private String ip;
}
