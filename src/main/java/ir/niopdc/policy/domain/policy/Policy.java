package ir.niopdc.policy.domain.policy;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "POLICY")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Policy {

    @Id
    private Integer id;
    private String name;
    private String currentVersion;

    private String publishTopicTitle;
    private String subscribeTopicTitle;
    private Long bigDelay;
    private Long slightDelay;
    private Integer maxSlightDelayTryCount;
    private Integer maxBigDelayTryCount;
    private Byte qos;
    private Boolean retain;
}
