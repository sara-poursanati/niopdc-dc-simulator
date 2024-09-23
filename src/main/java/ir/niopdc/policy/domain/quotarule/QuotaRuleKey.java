package ir.niopdc.policy.domain.quotarule;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuotaRuleKey {

    private String vehicleCode;
    private String f;
}
