package ir.niopdc.policy.domain.fuel;

import ir.niopdc.common.CsvConvertable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "FUEL")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Fuel implements CsvConvertable {

    @Id
    private String id;
    private String name;

    @Override
    public String convertToCsv(Object value) {
        StringBuilder builder = new StringBuilder();
        builder.append(getId());
        builder.append(",");
        builder.append(getName());

        return builder.toString();
    }
}
