package antifraud;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
public class StolenCard {
    @Id
    @GeneratedValue
    private long id;
    @NonNull
    private String number;

    public boolean luhnCheck() {
       return true;
    }
}
