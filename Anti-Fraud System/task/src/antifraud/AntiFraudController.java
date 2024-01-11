package antifraud;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

@RestController
public class AntiFraudController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public AntiFraudController(UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/api/antifraud/transaction")
    public TransactionDTO postTransaction(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setAllowedLimit(200);
        transaction.setManualLimit(1500);
        TransactionDTO response = new TransactionDTO();
        response.setResult(transaction.validateInput().toString());
        return response;
    }
    @PostMapping("/api/auth/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO postUser(@Valid @RequestBody UserRequest request) {
        if (userRepository.findUserByUsername(request.getUsername()).isPresent()) {
            throw new UserExistsException();
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setAuthority("USER");
        userRepository.save(user);
        UserDTO response = new UserDTO();
        response.setId(user.getId());
        response.setName(request.getName());
        response.setUsername(request.getUsername());
        return response;
    }
    @GetMapping("/api/auth/list")
    public ArrayList<UserDTO> getUsers() {
        ArrayList<UserDTO> response = new ArrayList<>();
        Iterable<User> dbResults = userRepository.findAll();
        dbResults.forEach(e -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(e.getId());
            userDTO.setName(e.getName());
            userDTO.setUsername(e.getUsername());
            response.add(userDTO);
        });
        return response;
    }

    @DeleteMapping("/api/auth/user")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser(@RequestParam(value = "username") String username) {
        System.out.println(username);
        Optional<User> dbResult = userRepository.findUserByUsername(username);
        dbResult.ifPresentOrElse(userRepository::delete, () -> { throw new UserNotFoundException(); });
        System.out.println(username);
        return "{ \"username:\"" + username + "," + "\"status\":" + "Deleted successfully!}";
    }
}
