package ir.niopdc.policy.domain.fuelrate;

import ir.niopdc.base.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuelRateService extends BaseService<FuelRateRepository, FuelRate, Integer> {
    public List<FuelRate> findByFuelIdVersion(Integer fuelId, String version) {
        return getRepository().findByFuelIdAndVersion(fuelId, version);
    }

    public List<FuelRate> findByVersion(String version) {
        return getRepository().findByVersion(version);
    }
}
