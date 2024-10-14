package ir.niopdc.policy.domain.regionalquotarule;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionalQuotaRuleRepository extends ListCrudRepository<RegionalQuotaRule, RegionalQuotaRuleKey> {
}
