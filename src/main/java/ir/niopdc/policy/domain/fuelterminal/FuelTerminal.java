package ir.niopdc.policy.domain.fuelterminal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "PT_INFO")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FuelTerminal {

    @EmbeddedId
    private FuelTerminalKey id;
    @Column(name = "FUEL_TYPE")
    private String fuelId;
}
