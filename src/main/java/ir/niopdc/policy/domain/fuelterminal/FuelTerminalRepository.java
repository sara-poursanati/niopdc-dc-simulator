package ir.niopdc.policy.domain.fuelterminal;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelTerminalRepository extends ListCrudRepository<FuelTerminal, FuelTerminalKey> {
    long countById_stationId(String station);
}
