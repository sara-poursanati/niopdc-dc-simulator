package ir.niopdc.policy.domain.fueltype;

import ir.niopdc.policy.common.CsvConvertable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FUEL_TYPE_INFO")
public class FuelTypeInfo implements CsvConvertable {

    @Id
    private String fuelType;
    private String fuelTypeName;
    private int p;
    private int p1;
    private int p2;
    private int p3;

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

    @Override
    public String convertToCsv(Object value) {
        StringBuilder builder = new StringBuilder();
        builder.append(getFuelType());
        builder.append(",");
        builder.append(getFuelTypeName());
        builder.append(",");
        builder.append(getP());
        builder.append(",");
        builder.append(getP1());
        builder.append(",");
        builder.append(getP2());
        builder.append(",");
        builder.append(getP3());

        return builder.toString();
    }
}
