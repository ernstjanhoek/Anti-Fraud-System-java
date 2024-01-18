package antifraud;

import antifraud.AntiFraudExceptions.FeedbackProcessingException;
import antifraud.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    Iterable<Transaction> findAllByNumber(String cardNumber);
    @Query("SELECT count(DISTINCT t.ip) FROM Transaction t " +
            "WHERE :ip <> t.ip " +
            "AND t.date BETWEEN :start_time AND :end_time " +
            "AND t.number = :number")
    Integer countDistinctIps(@Param("start_time") LocalDateTime startTime,
                             @Param("end_time") LocalDateTime endTime,
                             @Param("ip") String ipAddr,
                             @Param("number") String number
    );
    @Query("SELECT count(DISTINCT t.region) FROM Transaction t " +
            "WHERE :region <> t.region " +
            "AND t.date BETWEEN :start_time AND :end_time " +
            "AND t.number = :number")
    Integer countDistinctRegions(@Param("start_time") LocalDateTime startTime,
                                 @Param("end_time") LocalDateTime endTime,
                                 @Param("region") String region,
                                 @Param("number") String number
     );
    @Transactional
    default void setFeedback(Integer id, String feedback) {
        Optional<Transaction> entry = findById(id);
        entry.ifPresentOrElse(tx -> {
            if (feedback.equals(tx.getFeedback()) || feedback.equals(tx.getResult())) {
                throw new FeedbackProcessingException();
            }
            tx.setFeedback(feedback);
            save(tx);
        }, () -> {
            throw new EmptyResultDataAccessException(id);
        });
    }
}