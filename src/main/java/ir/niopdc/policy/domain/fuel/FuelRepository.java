package ir.niopdc.policy.domain.fuel;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelRepository extends ListCrudRepository<Fuel, String> {
}
