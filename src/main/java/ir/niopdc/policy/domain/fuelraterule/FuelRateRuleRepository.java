package ir.niopdc.policy.domain.fuelraterule;

import ir.niopdc.policy.domain.fuel.Fuel;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelRateRuleRepository extends ListCrudRepository<FuelRateRule, FuelRateRuleKey> {
}
