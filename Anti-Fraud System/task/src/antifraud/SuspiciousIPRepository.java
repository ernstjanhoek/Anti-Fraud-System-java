package antifraud;

import antifraud.SuspiciousIP;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface SuspiciousIPRepository  extends CrudRepository<SuspiciousIP, Integer> {
    Optional<SuspiciousIP> findSuspiciousIPByIpAddress(String IpAddress);

}
