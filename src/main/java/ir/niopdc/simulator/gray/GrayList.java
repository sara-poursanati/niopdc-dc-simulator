package ir.niopdc.simulator.gray;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GrayList {

    @Id
    private String card_id;

    private String valide_id;

    private String reason;

    private String type;

    private Timestamp date_time;

    private String user_id;

}
