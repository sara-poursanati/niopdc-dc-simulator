package ir.niopdc.policy.domain.regionalquotarule;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "regional_quota_rule")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class RegionalQuotaRule {

    @EmbeddedId
    private RegionalQuotaRuleKey id;
    private Long docId;
    private Integer quotaId;
    private Integer roleId;
    private Integer cf0;
    private Integer cf1;
    private Integer cf2;
    private Integer saving0;
    private Integer saving1;
    private Integer maxFuelCount;
    private Integer limitOneDay;
    private Integer limitOneTime;
    private Integer limitOneDuration;
    private Long duration;
}
