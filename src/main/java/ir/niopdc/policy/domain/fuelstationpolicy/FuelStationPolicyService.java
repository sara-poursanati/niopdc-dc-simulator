package ir.niopdc.policy.domain.fuelstationpolicy;

import ir.niopdc.base.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuelStationPolicyService extends BaseService<FuelStationPolicyRepository, FuelStationPolicy, FuelStationPolicyKey> {

    public List<FuelStationPolicy> findFuelStationPolicyByFuelStationId(String fuelStationId) {
        return getRepository().findById_FuelStationId(fuelStationId);
    }
}
