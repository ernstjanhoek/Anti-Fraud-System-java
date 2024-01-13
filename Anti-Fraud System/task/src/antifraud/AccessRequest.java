package antifraud;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccessRequest {
    @NotBlank
    private final String username;
    @Pattern(regexp = "LOCKED|UNLOCKED")
    private final String lockState;
}