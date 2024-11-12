package ir.niopdc.policy.domain.policyversion;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "policy_version")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyVersion {

    @EmbeddedId
    PolicyVersionKey id;
    private String versionName;
    @Column(length = 128)
    private String checksum;
    private ZonedDateTime releaseTime;
    private ZonedDateTime activationTime;
}
