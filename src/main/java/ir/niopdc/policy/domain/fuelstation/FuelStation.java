package ir.niopdc.policy.domain.fuelstation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "GS_INFO")
@Getter
@Setter
@NoArgsConstructor
public class FuelStation {

    @Id
    @Column(name = "GS_ID")
    private String id;
    private String areaId;
    @Column(name = "GS_NAME")
    private String name;
    @Column(name = "ADDR")
    private String address;
    private String zoneId;
    private String cityId;
    private String code;
    private Character type;
    private String inchargeMan;
    private String email;
    private String contactMan;
    private String contactTelephone;
    private String telephone1;
    private String telephone2;
    private String fax;
    private String zipCode;
    private LocalDateTime openDate;
    private int initCount;
    private Character dialupSign;
    private String dailySettleBegin;
    private String dailySettleEnd;
    private String owner;
    private Character initStatus;
    private Character validity;
//    @OneToMany(mappedBy = "fuelStation",
//            fetch = FetchType.EAGER,
//            cascade = CascadeType.ALL)
//    private List<FuelTerminal> fuelTerminals;

}
