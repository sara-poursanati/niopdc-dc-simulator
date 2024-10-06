package ir.niopdc.policy.domain.policyversion;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
public class PolicyVersionKey {
    private Integer policyId;
    private String version;
}
