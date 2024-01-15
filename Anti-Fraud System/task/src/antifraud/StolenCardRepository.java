package antifraud;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface StolenCardRepository extends CrudRepository<StolenCard, Integer> {
    Optional<StolenCard> findStolenCardByNumber(String number);
}
