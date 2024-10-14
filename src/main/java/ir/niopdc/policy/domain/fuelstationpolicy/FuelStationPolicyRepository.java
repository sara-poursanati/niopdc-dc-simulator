package ir.niopdc.policy.domain.fuelstationpolicy;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FuelStationPolicyRepository extends ListCrudRepository<FuelStationPolicy, FuelStationPolicyKey> {
}
