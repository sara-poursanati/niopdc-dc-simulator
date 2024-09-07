package ir.nifss.policy.domain.fueltypehistory;

import ir.nifss.policy.domain.fueltype.FuelTypeInfo;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "HISTORY_FUEL_TYPE")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class FuelTypeInfoHistory extends FuelTypeInfo {

    @EmbeddedId
    private FuelTypeInfoHistoryKey id;
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
}
