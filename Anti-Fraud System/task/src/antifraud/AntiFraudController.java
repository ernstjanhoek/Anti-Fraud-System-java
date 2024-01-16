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
        if (userRepository.findUserByUsername(principal.getName()).get().getLockstate().isState(LockState.LOCK)) {
            throw new LockStateException();
        }
        Transaction transaction = new Transaction(request.getAmount(), 200L, 1500L);
        Transaction.TransactionProcess processStatus = Transaction.TransactionProcess.ALLOWED;
        if (suspiciousIPRepository.findSuspiciousIPByIpAddress(request.getIp()).isPresent()) {
            processStatus = Transaction.TransactionProcess.PROHIBITED;
            transaction.appendInfo("ip");
        }
        if (stolenCardRepository.findStolenCardByNumber(request.getNumber()).isPresent()) {
            processStatus = Transaction.TransactionProcess.PROHIBITED;
            transaction.appendInfo("card-number");
        }
        if (request.getAmount() > transaction.getManualLimit()) {
            processStatus = Transaction.TransactionProcess.PROHIBITED;
            transaction.appendInfo("amount");
        } else if (request.getAmount() > transaction.getAllowedLimit() && processStatus == Transaction.TransactionProcess.ALLOWED) {
            processStatus = Transaction.TransactionProcess.MANUAL_PROCESSING;
            transaction.appendInfo("amount");
        }
        return new TransactionDTO(processStatus.toString(), transaction.buildInfoString());
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
    public StolenCardDTO postCard(@Valid @RequestBody StolenCardRequest request) {
        if (!LuhnCheck.cardNumValidation(request.getNumber()) || !LuhnCheck.isValidLuhn(request.getNumber())) {
            throw new InvalidInputException();
        }
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
        if (!LuhnCheck.cardNumValidation(number) || !LuhnCheck.isValidLuhn(number)) {
            throw new InvalidInputException();
        }
        Optional<StolenCard> dbResult = stolenCardRepository.findStolenCardByNumber(number);
        dbResult.ifPresentOrElse(stolenCardRepository::delete, () -> { throw new NotFoundException();});
        return "{ \"status\": \"Card " + dbResult.get().getNumber() + " successfully removed!\"}";
    }
    @PostMapping("/api/antifraud/suspicious-ip")
    public SuspiciousIPDTO postSuspiciousIP(@Valid @RequestBody SuspiciousIPRequest request) {
        if (!request.validateIP()) {
            throw new InvalidInputException();
        }
        if (suspiciousIPRepository.findSuspiciousIPByIpAddress(request.getIp()).isPresent()) {
            throw new ExistsException();
        }
        SuspiciousIP ip = new SuspiciousIP();
        ip.setIpAddress(request.getIp());
        suspiciousIPRepository.save(ip);
        return new SuspiciousIPDTO(ip.getId(), ip.getIpAddress());
    }
    @GetMapping("/api/antifraud/suspicious-ip")
    public ArrayList<SuspiciousIPDTO> getIP() {
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
        if (!ip.matches("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$")) {
            throw new InvalidInputException();
        }
        Optional<SuspiciousIP> dbResult = suspiciousIPRepository.findSuspiciousIPByIpAddress(ip);
        dbResult.ifPresentOrElse(suspiciousIPRepository::delete, () -> { throw new NotFoundException();});
        return "{ \"status\": \"IP " + dbResult.get().getIpAddress() + " successfully removed!\"}";
    }
}