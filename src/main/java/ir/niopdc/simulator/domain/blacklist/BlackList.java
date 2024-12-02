package ir.niopdc.simulator.domain.blacklist;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Entity
@Table(name = "incb_list")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BlackList {

    @Id
    private String cardId;
    private ZonedDateTime releaseTime;
    private ZonedDateTime activeTime;
    private String version;
    private String reason;

}
