package ir.niopdc.policy.domain.fuelrate;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelRateRepository extends ListCrudRepository<FuelRate, FuelRateKey> {
}
