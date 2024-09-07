package ir.nifss.policy.domain.fueltypehistory;

import ir.nifss.policy.domain.fueltype.FuelTypeInfoRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelTypeInfoHistoryRepository extends ListCrudRepository<FuelTypeInfoRepository, FuelTypeInfoHistoryKey> {
}
