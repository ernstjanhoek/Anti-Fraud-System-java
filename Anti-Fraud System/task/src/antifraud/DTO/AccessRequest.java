package antifraud.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class AccessRequest {
    @NotBlank
    private final String username;
    // @Pattern(regexp = "LOCK|UNLOCK")
    private final String operation;
}