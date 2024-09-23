package ir.niopdc.policy.domain.mediagateway;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "MEDIA_GATEWAY")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MediaGateway {

    @Id
    private String serialNumber;
    private String stationId;
}
