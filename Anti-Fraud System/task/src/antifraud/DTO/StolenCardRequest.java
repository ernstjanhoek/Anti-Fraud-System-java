package antifraud.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StolenCardRequest {
    @NotBlank
    private String number;
}
