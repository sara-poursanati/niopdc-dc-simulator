package ir.niopdc.policy.domain.coding;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "coding")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Coding {

    @Id
    private String cardId;
}
