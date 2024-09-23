package ir.niopdc.policy.domain.fuelraterule;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FuelRateRuleKey {

    private String fuelId;
    private int hourNumber;
}
