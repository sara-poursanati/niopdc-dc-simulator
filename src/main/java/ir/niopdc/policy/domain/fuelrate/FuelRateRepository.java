package ir.niopdc.policy.domain.fuelrate;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface FuelRateRepository extends ListCrudRepository<FuelRate, Integer> {
    List<FuelRate> findByFuelIdAndVersion(Integer fuelId, String version);
    List<FuelRate> findByVersion(String version);
}
