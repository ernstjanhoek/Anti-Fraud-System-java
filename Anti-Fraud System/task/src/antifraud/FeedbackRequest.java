package antifraud;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FeedbackRequest {
    @NotBlank
    long transsactionId;
    @Pattern(regexp = "ALLOWED|MANUAL_PROCESSING|PROHIBITED")
    String feedback;
}
