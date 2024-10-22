package ir.niopdc.policy.domain.fuelterminal;

import ir.niopdc.base.BaseService;
import ir.niopdc.policy.domain.fuelstation.FuelStation;
import org.springframework.stereotype.Service;

@Service
public class FuelTerminalService extends BaseService<FuelTerminalRepository, FuelTerminal, FuelTerminalKey> {

    public long getPtCountByFuelStation(String theFuelStationId) {
        return getRepository().countById_stationId(theFuelStationId);
    }

}
