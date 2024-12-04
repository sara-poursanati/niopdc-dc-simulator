package ir.niopdc.simulator.domain.graylist;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GrayList {

    @Id
    private String cardId;

    private String valideId;

    private String reason;

    @Enumerated(EnumType.STRING)
    private GrayListType type;

    private ZonedDateTime dateTime;

    private String userId;
}
