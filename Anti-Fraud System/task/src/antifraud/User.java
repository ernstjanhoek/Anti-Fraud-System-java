package antifraud;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String username;
    private String password;
    private String authority;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        // if (name.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        // }
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        // if (username.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        // }
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        // if (password.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        // }
        this.password = password;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
    public String getAuthority() {
        return authority;
    }
    public void setAuthority(String authority) {
        this.authority = authority;
    }
}