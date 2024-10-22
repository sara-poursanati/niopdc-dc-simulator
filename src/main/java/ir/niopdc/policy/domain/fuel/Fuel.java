package ir.niopdc.policy.domain.fuel;

import jakarta.persistence.Column;
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
public class Fuel {

    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
}
