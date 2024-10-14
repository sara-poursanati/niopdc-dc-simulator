package ir.niopdc.policy.domain.fuelrate;

import ir.niopdc.policy.domain.fuel.Fuel;
import jakarta.persistence.*;
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
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "fuel_id", nullable=false)
    private Fuel fuel;
    private Integer rateNumber;
    private Integer rateValue;
    private Integer feeValue;
}
