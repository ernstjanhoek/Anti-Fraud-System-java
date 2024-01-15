package antifraud;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import java.security.Principal;

@RestController
public class AntiFraudController {
    private final UserRepository userRepository;
    private final SuspiciousIPRepository suspiciousIPRepository;
    private final StolenCardRepository stolenCardRepository;
    private final PasswordEncoder passwordEncoder;
    public AntiFraudController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            SuspiciousIPRepository suspiciousIPRepository,
            StolenCardRepository stolenCardRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.suspiciousIPRepository = suspiciousIPRepository;
        this.stolenCardRepository = stolenCardRepository;
    }

    @PostMapping("/api/antifraud/transaction")
    public TransactionDTO postTransaction(Principal principal, @Valid @RequestBody TransactionRequest request) {
        if (userRepository.findUserByUsername(principal.getName()).get().getLockstate().isState(LockState.UNLOCK)) {
            long allowedLimit = 200;
            long manualLimit = 1500;
            Transaction transaction = new Transaction(
                    request.getAmount(),
                    allowedLimit,
                    manualLimit,
                    request.getIp(),
                    request.getNumber()
            );
            String info = "none";
            return new TransactionDTO(transaction.validateInput().toString(), info);
        } else {
            throw new LockStateException();
        }
    }

    @PostMapping("/api/auth/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO postUser(@Valid @RequestBody UserRequest request) {
        if (userRepository.findUserByUsername(request.getUsername()).isPresent()) {
            throw new ExistsException();
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
        dbResult.ifPresentOrElse(userRepository::delete, () -> { throw new NotFoundException(); });
        return "{ \"username\":\"" + username + "\"," + "\"status\":" + "\"Deleted successfully!\"}";
    }

    @PutMapping("/api/auth/role")
    public RoleDTO changeRole(@Valid @RequestBody RoleRequest request) {
        Optional<User> userOptional = userRepository.findUserByUsername(request.getUsername());
        userOptional.ifPresentOrElse(e -> {
            if (request.getRole().equals(e.getAuthority())) {
                throw new RoleConflictException();
            }
            e.setAuthority(request.getRole());
            userRepository.save(e);
        }, () -> { throw new NotFoundException(); });
        return new RoleDTO(
                userOptional.get().getId(),
                userOptional.get().getUsername(),
                userOptional.get().getName(),
                request.getRole());
    }

    @PutMapping("/api/auth/access")
    public AccessDTO changeAccess(@Valid @RequestBody AccessRequest request) { // }, @PathVariable String state) {
        Optional<User> userOptional = userRepository.findUserByUsername(request.getUsername());
        userOptional.ifPresentOrElse(e -> {
            e.setLockstate(LockState.valueOf(request.getOperation()));
            userRepository.save(e);
        }, () -> { throw new NotFoundException(); });
        return new AccessDTO("User " + request.getUsername() + " " + LockState.valueOf(request.getOperation().toUpperCase()) + "!");
    }
    @PostMapping("/api/antifraud/stolencard")
    @ResponseStatus(HttpStatus.CREATED)
    public StolenCardDTO postCard(@Valid @RequestBody StolenCardRequest request) {
        if (stolenCardRepository.findStolenCardByNumber(request.getNumber()).isPresent()) {
            throw new ExistsException();
        }
        StolenCard card = new StolenCard();
        card.setNumber(request.getNumber());
        stolenCardRepository.save(card);
        return new StolenCardDTO(card.getId(), card.getNumber());
    }
    @GetMapping("/api/antifraud/stolencard")
    public ArrayList<StolenCardDTO> getCards() {
        ArrayList<StolenCardDTO> response = new ArrayList<>();
        Iterable<StolenCard> dbResults = stolenCardRepository.findAll();
        dbResults.forEach(e -> response.add(
                new StolenCardDTO(
                        e.getId(),
                        e.getNumber()
                )
        ));
        return response;
    }
    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public String deleteCard(@PathVariable String number) {
        Optional<StolenCard> dbResult = stolenCardRepository.findStolenCardByNumber(number);
        dbResult.ifPresentOrElse(stolenCardRepository::delete, () -> { throw new NotFoundException();});
        return "{ \"status\": \"Card\"" + dbResult.get().getNumber() + "\" successfully removed!\"}";
    }
    @PostMapping("/api/antifraud/suspicious-ip")
    @ResponseStatus(HttpStatus.CREATED)
    public SuspiciousIPDTO postSuspiciousIP(@Valid @RequestBody SuspiciousIPRequest request) {
        if (suspiciousIPRepository.findSuspiciousIPByIpAddress(request.getIp()).isPresent()) {
            throw new ExistsException();
        }
        SuspiciousIP ip = new SuspiciousIP();
        ip.setIpAddress(request.getIp());
        suspiciousIPRepository.save(ip);
        return new SuspiciousIPDTO(ip.getId(), ip.getIpAddress());
    }
    @GetMapping("/api/antifraud/suspicious-ip")
    public ArrayList<SuspiciousIPDTO> fnGetIP() {
        ArrayList<SuspiciousIPDTO> response = new ArrayList<>();
        Iterable<SuspiciousIP> dbResults = suspiciousIPRepository.findAll();
        dbResults.forEach(e -> response.add(
                new SuspiciousIPDTO(
                        e.getId(),
                        e.getIpAddress()
                )
        ));
        return response;
    }
    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public String deleteIP(@PathVariable String ip) {
        Optional<SuspiciousIP> dbResult = suspiciousIPRepository.findSuspiciousIPByIpAddress(ip);
        dbResult.ifPresentOrElse(suspiciousIPRepository::delete, () -> { throw new NotFoundException();});
        return "{ \"status\": \"IP\"" + dbResult.get().getIpAddress() + "\" successfully removed!\"}";
    }
}