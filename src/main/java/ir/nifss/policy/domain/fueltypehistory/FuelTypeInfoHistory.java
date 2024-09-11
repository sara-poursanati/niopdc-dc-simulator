package ir.nifss.policy.domain.fueltypehistory;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "HISTORY_FUEL_TYPE")
public class FuelTypeInfoHistory {

    @EmbeddedId
    private FuelTypeInfoHistoryKey id;
    private String fuelTypeName;
    private int p;
    private int p1;
    private int p2;
    private int p3;
    private LocalDateTime releaseTime;
    private LocalDateTime activeTime;

    public FuelTypeInfoHistoryKey getId() {
        return id;
    }

    public void setId(FuelTypeInfoHistoryKey id) {
        this.id = id;
    }

    public LocalDateTime getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(LocalDateTime activeTime) {
        this.activeTime = activeTime;
    }

    public LocalDateTime getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(LocalDateTime releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getFuelTypeName() {
        return fuelTypeName;
    }

    public void setFuelTypeName(String fuelTypeName) {
        this.fuelTypeName = fuelTypeName;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getP1() {
        return p1;
    }

    public void setP1(int p1) {
        this.p1 = p1;
    }

    public int getP2() {
        return p2;
    }

    public void setP2(int p2) {
        this.p2 = p2;
    }

    public int getP3() {
        return p3;
    }

    public void setP3(int p3) {
        this.p3 = p3;
    }
}
