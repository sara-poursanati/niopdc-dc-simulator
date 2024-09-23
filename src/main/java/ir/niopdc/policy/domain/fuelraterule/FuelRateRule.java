package ir.niopdc.policy.domain.fuelraterule;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "FUEL_RATE_RULE")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FuelRateRule {

    @EmbeddedId
    private FuelRateRuleKey id;
    private int rateNumber;
}
