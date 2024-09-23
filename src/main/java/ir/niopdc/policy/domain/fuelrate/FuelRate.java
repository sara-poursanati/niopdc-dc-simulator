package ir.niopdc.policy.domain.fuelrate;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "FUEL_RATE")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FuelRate {

    @EmbeddedId
    private FuelRateKey id;
    private int rateValue;
}
