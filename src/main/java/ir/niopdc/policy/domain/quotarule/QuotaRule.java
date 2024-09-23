package ir.niopdc.policy.domain.quotarule;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "QUOTA_INFO")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuotaRule {

    @EmbeddedId
    private QuotaRuleKey id;
    private Long a;
    private Long b;
    private Long d;
    private Long t;
    private Long v;
    private Long w;
    private Long q;
    private Long x1;
    private Long x2;
    private Long x3;
    private Long rfu;
}
