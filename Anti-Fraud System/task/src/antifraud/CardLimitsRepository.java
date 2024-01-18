package antifraud;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface CardLimitsRepository extends CrudRepository<CardLimits, Integer> {
    Optional<CardLimits> findById(String number);
}
