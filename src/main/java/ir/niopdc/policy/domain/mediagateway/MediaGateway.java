package ir.niopdc.policy.domain.mediagateway;

import ir.niopdc.policy.domain.fuelstation.FuelStation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "media_gateway")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MediaGateway {

    @Id
    private String serialNumber;
//    private String stationId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "station_id", referencedColumnName = "GS_ID")
    private FuelStation fuelStation;
}
