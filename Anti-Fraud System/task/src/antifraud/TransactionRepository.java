package antifraud;

import antifraud.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

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
}