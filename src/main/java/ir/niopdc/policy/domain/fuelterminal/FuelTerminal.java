package ir.niopdc.policy.domain.fuelterminal;

import ir.niopdc.policy.domain.fuelstation.FuelStation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

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

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH,
                    CascadeType.REFRESH, CascadeType.MERGE,
                    CascadeType.PERSIST}
    )
    @JoinColumn(name = "fuel_station_id", updatable = false)
    private FuelStation fuelStation;

    @Column(name = "fuel_sam_id")
    private String fuelSamId;

    @Column(name = "pay_sam_id")
    private String paySamId;

    @Column(name = "ipc_ip_addr")
    private String ipcIpAddr;

    @Column(name = "pt_ip_addr")
    private String ptIpAddr;

    @Column(name = "nozzle_id")
    private String nozzleId;

    @Column(name = "oilcan_id")
    private String oilcanId;

    @Column(name = "dispenser_type")
    private String dispenserType;

    @Column(name = "f_fuel_public_key_n")
    private String fuelPublicKeyN;

    @Column(name = "f_pay_public_key_n")
    private String fPayPublicKeyN;

    @Column(name = "s_fuel_public_key_n")
    private String sFuelPublicKeyN;

    @Column(name = "s_pay_public_key_n")
    private String sPayPublicKeyN;

    @Column(name = "f_fuel_public_key_e")
    private String fuelPublicKeyE;

    @Column(name = "f_pay_public_key_e")
    private String fPayPublicKeyE;

    @Column(name = "s_fuel_public_key_e")
    private String sFuelPublicKeyE;

    @Column(name = "s_pay_public_key_e")
    private String sPayPublicKeyE;

    @Column(name = "init_status")
    private Character initStatus;

    @Column(name = "f_cardissue_status")
    private Character fCardissueStatus;

    @Column(name = "p_cardissue_status")
    private Character pCardissueStatus;

    @Column(name = "app_issuer")
    private String appIssuer;

    @Column(name = "app_receiver")
    private String appReceiver;

    @Column(name = "app_date_begin")
    private Date appDateBegin;

    @Column(name = "app_date_end")
    private Date appDateEnd;

    @Column(name = "st_app_issuer")
    private String stAppIssuer;

    @Column(name = "st_app_receiver")
    private String stAppReceiver;

    @Column(name = "st_app_date_begin")
    private Date stAppDateBegin;

    @Column(name = "st_app_date_end")
    private Date stAppDateEnd;

    @Column(name = "sam_seria")
    private String samSeria;

    @Column(name = "sam_ver")
    private String samVer;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "fci_by_issuer")
    private String fciByIssuer;

    @Column(name = "con_key_idx")
    private String conKeyIdx;

    @Column(name = "f_batch_name")
    private String fBatchName;

    @Column(name = "p_batch_name")
    private String pBatchName;

    @Column(name = "apply_time")
    private Date applyTime;

    @Column(name = "validity")
    private Character validity;

}
