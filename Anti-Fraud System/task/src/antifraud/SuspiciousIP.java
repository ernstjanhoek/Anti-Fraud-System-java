package antifraud;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "suspicious_ip")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class SuspiciousIP {
    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    @Column(name = "ip")
    private String ipAddress;
}