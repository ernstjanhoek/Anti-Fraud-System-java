package antifraud;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
@Data
public class RoleRequest {
    @NotBlank
    private final String username;
    @Pattern(regexp = "SUPPORT|MERCHANT")
    private final String role;
}
