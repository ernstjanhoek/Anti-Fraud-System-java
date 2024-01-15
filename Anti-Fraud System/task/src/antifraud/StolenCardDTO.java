package antifraud;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StolenCardDTO {
    private long id;
    private String number;
}
