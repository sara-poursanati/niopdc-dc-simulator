package ir.niopdc.policy.domain.fuelstationpolicy;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "fuel_station_policy")
@Getter
@Setter
@ToString
public class FuelStationPolicy {

    @EmbeddedId
    private FuelStationPolicyKey id;
    private String publishTopicTitle;
    private String subscribeTopicTitle;
    private Long bigDelay;
    private Long slightDelay;
    private Integer maxSlightDelayTryCount;
    private Integer maxBigDelayTryCount;
    private Byte qos;
    private Boolean retain;
}
