package antifraud.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StolenCardResponse {
    private long id;
    private String number;
}
