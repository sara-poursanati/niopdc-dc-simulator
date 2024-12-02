package ir.niopdc.simulator.domain.grayhistory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GrayHistory {

    @Id
    private String cardId;

    private String validId;

    private String reason;

    private String type;

    private String userId;

    private Timestamp dmTime;

    private String dmType = "D";
}
