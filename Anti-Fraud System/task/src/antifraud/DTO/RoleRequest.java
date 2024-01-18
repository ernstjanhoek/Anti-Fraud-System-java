package antifraud.DTO;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class RoleRequest {
    @NotBlank
    private final String username;
    @Pattern(regexp = "SUPPORT|MERCHANT")
    private final String role;
}
