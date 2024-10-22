package ir.niopdc.policy.domain.fuelterminal;

import ir.niopdc.base.BaseService;
import ir.niopdc.policy.domain.fuelstation.FuelStation;
import org.springframework.stereotype.Service;

@Service
public class FuelTerminalService extends BaseService<FuelTerminalRepository, FuelTerminal, FuelTerminalKey> {

    public long getPtCountByFuelStation(String fuelStationId) {
        return getRepository().countById_stationId(fuelStationId);
    }

}
