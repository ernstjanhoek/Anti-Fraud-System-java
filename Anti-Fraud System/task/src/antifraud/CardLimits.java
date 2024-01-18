package antifraud;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardLimits {
    @Id
    @Column(unique = true)
    private String id;
    private long allowedLimit;
    private long prohibitedLimit;
}