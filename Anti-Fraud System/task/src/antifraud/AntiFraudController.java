package antifraud;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
public class AntiFraudController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public AntiFraudController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/api/antifraud/transaction")
    public TransactionDTO postTransaction(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = new Transaction(request.getAmount(), 200, 1500);
        return new TransactionDTO(transaction.validateInput().toString());
    }
    @PostMapping("/api/auth/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO postUser(@Valid @RequestBody UserRequest request) {
        if (userRepository.findUserByUsername(request.getUsername()).isPresent()) {
            throw new UserExistsException();
        }
        User user = new User(request.getUsername(),
                request.getName(),
                passwordEncoder.encode(request.getPassword()));
        if (userRepository.count() == 0) {
            user.setAuthority("ADMINISTRATOR");
        } else {
            user.setAuthority("MERCHANT");
        }
        userRepository.save(user);
        return new UserDTO(user.getId(), request.getUsername(), request.getName(), user.getAuthority());
    }
    @GetMapping("/api/auth/list")
    public ArrayList<UserDTO> getUsers() {
        ArrayList<UserDTO> response = new ArrayList<>();
        Iterable<User> dbResults = userRepository.findAll();
        dbResults.forEach(e -> response.add(
                new UserDTO(
                        e.getId(),
                        e.getUsername(),
                        e.getName(),
                        e.getAuthority()
                )));
        return response;
    }
    @DeleteMapping("/api/auth/user/{username}")
    public String deleteUser(@Valid @NotBlank @PathVariable String username) {
        Optional<User> dbResult = userRepository.findUserByUsername(username);
        dbResult.ifPresentOrElse(userRepository::delete, () -> { throw new UserNotFoundException(); });
        return "{ \"username:\"" + username + "," + "\"status\":" + "Deleted successfully!}";
    }
    @PutMapping("/api/auth/role")
    public RoleDTO changeRole(@Valid @RequestBody RoleRequest request) {
        Optional<User> userOptional = userRepository.findUserByUsername(request.getUsername());
        userOptional.ifPresentOrElse(e -> {
            e.setAuthority(request.getRole());
            userRepository.save(e);
        }, () -> { throw new UserNotFoundException(); });
        return new RoleDTO(
                userOptional.get().getId(),
                userOptional.get().getUsername(),
                userOptional.get().getName(),
                request.getRole());
    }
    @PutMapping("/api/auth/access/")
    public AccessDTO changeAccess(@RequestBody AccessRequest request) {
        Optional<User> userOptional = userRepository.findUserByUsername(request.getUsername());
        userOptional.ifPresentOrElse(e -> {
            e.setLockstate(LockState.valueOf(request.getLockState()));
            userRepository.save(e);
        }, () -> { throw new UserNotFoundException(); });
        return new AccessDTO("User " + request.getUsername() + " " + request.getLockState());
    }
}