package ir.niopdc.policy.domain.fuel;

import ir.niopdc.policy.domain.fuelrate.FuelRate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "FUEL")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Fuel {

    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "fuel")
    private Set<FuelRate> rates;
}
