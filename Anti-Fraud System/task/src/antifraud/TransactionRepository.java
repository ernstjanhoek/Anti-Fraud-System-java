package antifraud;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    @Query("SELECT count(DISTINCT t.ip) FROM Transaction t") // WHERE t.date >= :time")
    Integer checkIp(); //@Param("time") LocalDateTime time);

    // @Query("Select count(*) from Transaction t where t.date >= :time LIMIT 3")
    // Integer checkRegion(@Param("time") LocalDateTime time);
}
