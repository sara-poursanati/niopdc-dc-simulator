package ir.niopdc.policy.domain.fuelterminal;

import jakarta.persistence.Column;
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
public class FuelTerminalKey {

    @Column(name = "GS_ID")
    private String stationId;
    @Column(name = "PT_ID")
    private String terminalId;
}
