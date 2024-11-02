package ir.niopdc.policy.domain.graylist;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Entity
@Table(name = "GRAY_LIST")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class GrayList {

    @Id
    private String cardId;
    private int type;
    private int reason;
    private ZonedDateTime insertionDateTime;
}
