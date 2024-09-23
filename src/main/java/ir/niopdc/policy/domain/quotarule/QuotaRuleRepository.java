package ir.niopdc.policy.domain.quotarule;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotaRuleRepository  extends ListCrudRepository<QuotaRule, QuotaRuleKey> {
}
