package ir.niopdc.policy.domain.fuelstation;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelStationRepository extends ListCrudRepository<FuelStation, String> {
}
