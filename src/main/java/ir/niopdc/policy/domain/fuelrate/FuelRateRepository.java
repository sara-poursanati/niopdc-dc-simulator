package ir.niopdc.policy.domain.fuelrate;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FuelRateRepository extends ListCrudRepository<FuelRate, Integer> {
}
