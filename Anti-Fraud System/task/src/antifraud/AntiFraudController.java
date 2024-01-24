package antifraud;

import antifraud.AntiFraudExceptions.*;
import antifraud.DTO.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private final TransactionRepository transactionRepository;
    private final CardLimitsRepository cardLimitsRepository;
    private final PasswordEncoder passwordEncoder;
    public AntiFraudController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            SuspiciousIPRepository suspiciousIPRepository,
            StolenCardRepository stolenCardRepository,
            TransactionRepository transactionRepository,
            CardLimitsRepository cardLimitsRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.suspiciousIPRepository = suspiciousIPRepository;
        this.stolenCardRepository = stolenCardRepository;
        this.transactionRepository = transactionRepository;
        this.cardLimitsRepository = cardLimitsRepository;
    }
    @PostMapping("/api/antifraud/transaction")
    public TransactionResponse postTransaction(Principal principal, @Valid @RequestBody TransactionRequest request) {
        if (userRepository.findUserByUsername(principal.getName()).get().getLockstate().isState(LockState.LOCK)) {
            throw new LockStateException();
        }

        final long[] allowedLimit = {200};
        final long[] prohibitedLimit = {1500};

        cardLimitsRepository
                .findById(request.getNumber())
                .ifPresentOrElse(
                        l -> {
                            allowedLimit[0] = l.getAllowedLimit();
                            prohibitedLimit[0] = l.getProhibitedLimit();
                        },
                        () -> {
                            cardLimitsRepository.save(
                                            new CardLimits(
                                                    request.getNumber(),
                                                    allowedLimit[0],
                                                    prohibitedLimit[0]
                                            )
                                    );
                        }
                );

        Transaction transaction = new Transaction(
                request.getAmount(),
                request.getIp(),
                request.getNumber(),
                request.getRegion(),
                request.getDate(),
                allowedLimit[0],
                prohibitedLimit[0]
        );

        int ipCheck = transactionRepository.countDistinctIps(
                request.getDate().minusSeconds(3600),
                request.getDate(),
                request.getIp(),
                request.getNumber()
        );

        int regionCheck = transactionRepository.countDistinctRegions(
                request.getDate().minusSeconds(3600),
                request.getDate(),
                request.getRegion(),
                request.getNumber()
        );

        Transaction.TransactionProcess processStatus = Transaction.TransactionProcess.ALLOWED;
        if (suspiciousIPRepository.findSuspiciousIPByIpAddress(request.getIp()).isPresent()) {
            processStatus = Transaction.TransactionProcess.PROHIBITED;
            transaction.appendInfo("ip");
        }
        if (stolenCardRepository.findStolenCardByNumber(request.getNumber()).isPresent()) {
            processStatus = Transaction.TransactionProcess.PROHIBITED;
            transaction.appendInfo("card-number");
        }
        if (ipCheck >= 3) {
            processStatus = Transaction.TransactionProcess.PROHIBITED;
            transaction.appendInfo("ip-correlation");
        }
        if (regionCheck >= 3) {
            processStatus = Transaction.TransactionProcess.PROHIBITED;
            transaction.appendInfo("region-correlation");
        }
        if (ipCheck >= 2 && processStatus != Transaction.TransactionProcess.PROHIBITED) {
            processStatus = Transaction.TransactionProcess.MANUAL_PROCESSING;
            transaction.appendInfo("ip-correlation");
        }
        if (regionCheck >= 2 && processStatus != Transaction.TransactionProcess.PROHIBITED) {
            processStatus = Transaction.TransactionProcess.MANUAL_PROCESSING;
            transaction.appendInfo("region-correlation");
        }
        if (request.getAmount() > transaction.getManualLimit()) {
            processStatus = Transaction.TransactionProcess.PROHIBITED;
            transaction.appendInfo("amount");
        } else if (request.getAmount() > transaction.getAllowedLimit() && processStatus != Transaction.TransactionProcess.PROHIBITED) {
            processStatus = Transaction.TransactionProcess.MANUAL_PROCESSING;
            transaction.appendInfo("amount");
        }
        transaction.setResult(processStatus.toString());
        transactionRepository.save(transaction);
        return new TransactionResponse(processStatus.toString(), transaction.buildInfoString());
    }

    @PostMapping("/api/auth/user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse postUser(@Valid @RequestBody UserRequest request) {
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
        return new UserResponse(user.getId(), request.getUsername(), request.getName(), user.getAuthority());
    }

    @GetMapping("/api/auth/list")
    public ArrayList<UserResponse> getUsers() {
        ArrayList<UserResponse> response = new ArrayList<>();
        Iterable<User> dbResults = userRepository.findAll();
        dbResults.forEach(e -> response.add(
                new UserResponse(
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
    public RoleResponse changeRole(@Valid @RequestBody RoleRequest request) {
        Optional<User> userOptional = userRepository.findUserByUsername(request.getUsername());
        userOptional.ifPresentOrElse(e -> {
            if (request.getRole().equals(e.getAuthority())) {
                throw new RoleConflictException();
            }
            e.setAuthority(request.getRole());
            userRepository.save(e);
        }, () -> { throw new NotFoundException(); });
        return new RoleResponse(
                userOptional.get().getId(),
                userOptional.get().getUsername(),
                userOptional.get().getName(),
                request.getRole());
    }
    @PutMapping("/api/auth/access")
    public AccessResponse changeAccess(@Valid @RequestBody AccessRequest request) { // }, @PathVariable String state) {
        Optional<User> userOptional = userRepository.findUserByUsername(request.getUsername());
        userOptional.ifPresentOrElse(e -> {
            e.setLockstate(LockState.valueOf(request.getOperation()));
            userRepository.save(e);
        }, () -> { throw new NotFoundException(); });
        return new AccessResponse("User " + request.getUsername() + " " + LockState.valueOf(request.getOperation().toUpperCase()) + "!");
    }
    @PostMapping("/api/antifraud/stolencard")
    public StolenCardResponse postCard(@Valid @RequestBody StolenCardRequest request) {
        if (!LuhnCheck.cardNumValidation(request.getNumber()) || !LuhnCheck.isValidLuhn(request.getNumber())) {
            throw new InvalidInputException();
        }
        if (stolenCardRepository.findStolenCardByNumber(request.getNumber()).isPresent()) {
            throw new ExistsException();
        }
        StolenCard card = new StolenCard();
        card.setNumber(request.getNumber());
        stolenCardRepository.save(card);
        return new StolenCardResponse(card.getId(), card.getNumber());
    }
    @GetMapping("/api/antifraud/stolencard")
    public ArrayList<StolenCardResponse> getCards() {
        ArrayList<StolenCardResponse> response = new ArrayList<>();
        Iterable<StolenCard> dbResults = stolenCardRepository.findAll();
        dbResults.forEach(e -> response.add(
                new StolenCardResponse(
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
    public SuspiciousIpResponse postSuspiciousIP(@Valid @RequestBody SuspiciousIPRequest request) {
        if (suspiciousIPRepository.findSuspiciousIPByIpAddress(request.getIp()).isPresent()) {
            throw new ExistsException();
        }
        SuspiciousIP ip = new SuspiciousIP();
        ip.setIpAddress(request.getIp());
        suspiciousIPRepository.save(ip);
        return new SuspiciousIpResponse(ip.getId(), ip.getIpAddress());
    }
    @GetMapping("/api/antifraud/suspicious-ip")
    public ArrayList<SuspiciousIpResponse> getIP() {
        ArrayList<SuspiciousIpResponse> response = new ArrayList<>();
        Iterable<SuspiciousIP> dbResults = suspiciousIPRepository.findAll();
        dbResults.forEach(e -> response.add(
                new SuspiciousIpResponse(
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
    @GetMapping("/api/antifraud/history")
    public ArrayList<FeedbackResponse> getHistory() {
        ArrayList<FeedbackResponse> response = new ArrayList<>();
        Iterable<Transaction> transactionsList =  transactionRepository.findAll();
        transactionsList.forEach(tx -> response.add(FeedbackResponse.fromTransaction(tx)));
        return response;
    }
    @GetMapping("/api/antifraud/history/{number}")
    public ArrayList<FeedbackResponse> getCardHistory(@PathVariable String number) {
        if (!LuhnCheck.cardNumValidation(number) || !LuhnCheck.isValidLuhn(number)) {
            throw new InvalidInputException();
        }
        ArrayList<FeedbackResponse> response = new ArrayList<>();
        Iterable<Transaction> transactionsList =  transactionRepository.findAllByNumber(number);
        transactionsList.forEach(tx -> response.add(FeedbackResponse.fromTransaction(tx)));
        if (response.isEmpty()) {
            throw new NotFoundException();
        }
        return response;
    }
    @PutMapping("/api/antifraud/transaction")
    public FeedbackResponse setFeedback(@Valid @RequestBody FeedbackRequest request) {
        Optional<Transaction> transaction = transactionRepository.findById(request.getTransactionId());
        transaction.ifPresentOrElse(tx -> {
                    if (request.getFeedback().equals(tx.getResult())) {
                        throw new FeedbackProcessingException();
                    }
                    if (!tx.getFeedback().isEmpty()) {
                        throw new FeedbackAlreadySetException();
                    } else {
                        tx.setFeedback(request.getFeedback());
                        transactionRepository.save(tx);
                    }
                }, () -> {
                    throw new EmptyResultDataAccessException(request.getTransactionId());
                }
        );
        Optional<CardLimits> cardLimitEntry = cardLimitsRepository.findById(transaction.get().getNumber());
        cardLimitEntry.ifPresentOrElse(e -> {
            if (request.getFeedback().equals("ALLOWED") && transaction.get().getResult().equals("PROHIBITED")) {
                e.setAllowedLimit(CardLimitMath.increase(e.getAllowedLimit(), transaction.get().getAmount()));
                e.setProhibitedLimit(CardLimitMath.increase(e.getProhibitedLimit(), transaction.get().getAmount()));
            }
            if (request.getFeedback().equals("ALLOWED") && transaction.get().getResult().equals("MANUAL_PROCESSING")) {
                e.setAllowedLimit(CardLimitMath.increase(e.getAllowedLimit(), transaction.get().getAmount()));
            }
            if (request.getFeedback().equals("MANUAL_PROCESSING") && transaction.get().getResult().equals("PROHIBITED")) {
                e.setProhibitedLimit(CardLimitMath.increase(e.getProhibitedLimit(), transaction.get().getAmount()));
            }
            if (request.getFeedback().equals("MANUAL_PROCESSING") && transaction.get().getResult().equals("ALLOWED")) {
                e.setAllowedLimit(CardLimitMath.decrease(e.getAllowedLimit(), transaction.get().getAmount()));
            }
            if (request.getFeedback().equals("PROHIBITED") && transaction.get().getResult().equals("MANUAL_PROCESSING")) {
                e.setProhibitedLimit(CardLimitMath.decrease(e.getProhibitedLimit(), transaction.get().getAmount()));
            }
            if (request.getFeedback().equals("PROHIBITED") && transaction.get().getResult().equals("ALLOWED")) {
                e.setProhibitedLimit(CardLimitMath.decrease(e.getProhibitedLimit(), transaction.get().getAmount()));
                e.setAllowedLimit(CardLimitMath.decrease(e.getAllowedLimit(), transaction.get().getAmount()));
            }
            cardLimitsRepository.save(e);
        }, () -> {
            throw new NotFoundException();
        });
        return FeedbackResponse.fromTransaction(transactionRepository.findById(request.transactionId).get());
    }
}
