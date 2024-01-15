package antifraud;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import lombok.*;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    private String username;
    @NonNull
    private String name;
    @NonNull
    private String password;
    private String authority;
    private LockState lockstate = LockState.LOCK;
}