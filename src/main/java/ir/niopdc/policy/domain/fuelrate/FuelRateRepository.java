package ir.niopdc.policy.domain.fuelrate;

import ir.niopdc.policy.domain.fuel.Fuel;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelRateRepository extends ListCrudRepository<FuelRate, FuelRateKey> {
}
