package antifraud;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleDTO {
    private long id;
    private String username;
    private String name;
    private String role;
}
