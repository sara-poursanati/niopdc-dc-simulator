package ir.niopdc.policy.domain.fueltype;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelTypeInfoRepository extends ListCrudRepository<FuelTypeInfo, String> {
}
