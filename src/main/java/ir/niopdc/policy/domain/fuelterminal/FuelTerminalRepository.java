package ir.niopdc.policy.domain.fuelterminal;

import ir.niopdc.policy.domain.fuelstationpolicy.FuelStationPolicy;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelTerminalRepository extends ListCrudRepository<FuelTerminal, FuelTerminalKey> {
    long countById_stationId(String station);
}
