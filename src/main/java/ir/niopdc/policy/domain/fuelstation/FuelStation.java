package ir.niopdc.policy.domain.fuelstation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "GS_INFO")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class FuelStation {

    @Id
    @Column(name = "GS_ID")
    private String id;
    private String areaId;
    @Column(name = "GS_NAME")
    private String name;
    @Column(name = "ADDR")
    private String address;
}
