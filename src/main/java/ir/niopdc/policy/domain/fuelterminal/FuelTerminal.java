package ir.niopdc.policy.domain.fuelterminal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

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

//    @ManyToOne(
//            fetch = FetchType.LAZY,
//            cascade = {CascadeType.DETACH,
//                    CascadeType.REFRESH, CascadeType.MERGE,
//                    CascadeType.PERSIST}
//    )
//    @JoinColumn(name = "gs_id", updatable = false)
//    private FuelStation fuelStation;
    private String fuelSamId;
    private String paySamId;
    private String ipcIpAddr;
    private String ptIpAddr;
    private String nozzleId;
    private String oilcanId;
    private String dispenserType;
    private String fuelPublicKeyN;
    private String fPayPublicKeyN;
    private String sFuelPublicKeyN;
    private String sPayPublicKeyN;
    private String fuelPublicKeyE;
    private String fPayPublicKeyE;
    private String sFuelPublicKeyE;
    private String sPayPublicKeyE;
    private Character initStatus;
    private Character fCardissueStatus;
    private Character pCardissueStatus;
    private String appIssuer;
    private String appReceiver;
    private LocalDateTime appDateBegin;
    private LocalDateTime appDateEnd;
    private String stAppIssuer;
    private String stAppReceiver;
    private LocalDateTime stAppDateBegin;
    private LocalDateTime stAppDateEnd;
    private String samSeria;
    private String samVer;
    private String cardType;
    private String fciByIssuer;
    private String conKeyIdx;
    private String fBatchName;
    private String pBatchName;
    private LocalDateTime applyTime;
    private Character validity;

}
