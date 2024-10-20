package ir.niopdc.policy.domain.policyversion;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "policy_version")
@Getter
@Setter
@ToString
public class PolicyVersion {

    @EmbeddedId
    PolicyVersionKey id;
    private String versionName;
    private ZonedDateTime releaseTime;
    private ZonedDateTime activationTime;

}
