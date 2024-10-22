package ir.niopdc.policy.domain.blacklist;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "black_list")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BlackList {

    @Id
    private String cardId;
    private ZonedDateTime insertionDateTime;
}
