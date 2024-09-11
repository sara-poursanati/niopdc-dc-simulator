package ir.nifss.policy.domain.fueltypehistory;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelTypeInfoHistoryRepository extends ListCrudRepository<FuelTypeInfoHistory, FuelTypeInfoHistoryKey> {
}
