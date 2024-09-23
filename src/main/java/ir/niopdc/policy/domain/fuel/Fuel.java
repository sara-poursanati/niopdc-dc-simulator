package ir.niopdc.policy.domain.fuel;

import ir.niopdc.common.CsvConvertable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FUEL_TYPE_INFO")
public class Fuel implements CsvConvertable {

    @Id
    private String fuelType;
    private String fuelTypeName;

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getFuelTypeName() {
        return fuelTypeName;
    }

    public void setFuelTypeName(String fuelTypeName) {
        this.fuelTypeName = fuelTypeName;
    }

    @Override
    public String convertToCsv(Object value) {
        StringBuilder builder = new StringBuilder();
        builder.append(getFuelType());
        builder.append(",");
        builder.append(getFuelTypeName());

        return builder.toString();
    }
}
