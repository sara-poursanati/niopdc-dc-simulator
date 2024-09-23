package ir.niopdc.policy.domain.localquotarule;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalQuotaRuleRepository extends ListCrudRepository<LocalQuotaRule, LocalQuotaRuleKey> {
}
