package antifraud;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    @Query("SELECT count(DISTINCT t.ip) FROM Transaction t WHERE t.date >= :time AND t.ip != :ip")
    Integer checkIp(@Param("time") LocalDateTime time, @Param("ip") String ip);

    @Query("SELECT count(DISTINCT t.region) FROM Transaction t WHERE t.date >= :time AND t.region != :region ")
    Integer checkRegion(@Param("time") LocalDateTime time, @Param("region") String region);
}
