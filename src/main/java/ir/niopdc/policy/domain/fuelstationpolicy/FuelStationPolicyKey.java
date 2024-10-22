package ir.niopdc.policy.domain.fuelstationpolicy;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
public class FuelStationPolicyKey {
    private int policyId;
    private String fuelStationId;
}
