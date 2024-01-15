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
    @Pattern(regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$" )
    @Column(name = "ip")
    private String ipAddress;
}