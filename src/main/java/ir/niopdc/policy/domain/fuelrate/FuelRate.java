package ir.niopdc.policy.domain.fuelrate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "FUEL_RATE_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FuelRate {
    @Id
    private Integer id;
    private Integer fuelId;
    private String version;
    private Integer rateNumber;
    private Integer rateValue;
    private Integer feeValue;
}
