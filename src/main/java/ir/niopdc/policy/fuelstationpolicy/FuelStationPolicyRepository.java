package ir.niopdc.policy.fuelstationpolicy;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelStationPolicyRepository extends ListCrudRepository<FuelStationPolicy, FuelStationPolicyKey> {
}
