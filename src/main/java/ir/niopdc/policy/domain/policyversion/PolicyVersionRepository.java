package ir.niopdc.policy.domain.policyversion;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyVersionRepository extends ListCrudRepository<PolicyVersion, PolicyVersionKey> {
}
