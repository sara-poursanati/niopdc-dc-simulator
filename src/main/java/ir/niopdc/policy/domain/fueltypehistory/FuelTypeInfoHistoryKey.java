package ir.niopdc.policy.domain.fueltypehistory;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FuelTypeInfoHistoryKey {
    private String fuelType;
    @Column(name = "ver")
    private String version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FuelTypeInfoHistoryKey that = (FuelTypeInfoHistoryKey) o;
        return fuelType.equals(that.fuelType) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        int result = fuelType.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }
}
