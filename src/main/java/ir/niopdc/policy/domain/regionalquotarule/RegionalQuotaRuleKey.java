package ir.niopdc.policy.domain.regionalquotarule;

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
public class RegionalQuotaRuleKey {

    private String stationId;
    private Integer catId;
}
