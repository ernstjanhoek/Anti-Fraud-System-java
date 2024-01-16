package antifraud;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SuspiciousIPRequest {
    @NotBlank
    private String ip;
    public boolean validateIP() {
        return this.ip.matches("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$");
    }
}
