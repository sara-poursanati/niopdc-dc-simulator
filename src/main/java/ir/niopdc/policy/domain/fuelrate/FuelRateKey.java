package ir.niopdc.policy.domain.fuelrate;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FuelRateKey {

    private String fuelId;
    private int rateNumber;

}
