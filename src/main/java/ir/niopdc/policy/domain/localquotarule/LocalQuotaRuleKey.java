package ir.niopdc.policy.domain.localquotarule;

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
public class LocalQuotaRuleKey {

    private String vehicleCode;
    private String f;
}
