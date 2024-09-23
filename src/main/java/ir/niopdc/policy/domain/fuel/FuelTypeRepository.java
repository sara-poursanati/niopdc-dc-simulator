package ir.niopdc.policy.domain.fuel;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelTypeRepository extends ListCrudRepository<Fuel, String> {
}
