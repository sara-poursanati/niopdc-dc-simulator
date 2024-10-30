package ir.niopdc.policy.domain.policyversion;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyVersionKey {
    private Byte policyId;
    private String version;
}
