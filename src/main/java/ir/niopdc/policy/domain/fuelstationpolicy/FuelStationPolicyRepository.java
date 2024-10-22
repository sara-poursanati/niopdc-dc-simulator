package ir.niopdc.policy.domain.fuelstationpolicy;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface FuelStationPolicyRepository extends ListCrudRepository<FuelStationPolicy, FuelStationPolicyKey> {
    List<FuelStationPolicy> findById_FuelStationId(String fuelStationId);
}
