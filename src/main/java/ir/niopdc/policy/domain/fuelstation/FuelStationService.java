package ir.niopdc.policy.domain.fuelstation;

import ir.niopdc.base.BaseService;
import ir.niopdc.policy.domain.mediagateway.MediaGateway;
import org.springframework.stereotype.Service;

@Service
public class FuelStationService extends BaseService<FuelStationRepository, FuelStation, String> {
    public FuelStation getFuelStationByMediaGateway(MediaGateway mediaGateway) {
        return mediaGateway.getFuelStation();
    }
}
