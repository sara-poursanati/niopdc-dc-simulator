package ir.niopdc.policy.domain.coding;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Entity
@Table(name = "coding_list")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CodingList {

    @Id
    private String cardId;
    private ZonedDateTime insertionDateTime;
}
