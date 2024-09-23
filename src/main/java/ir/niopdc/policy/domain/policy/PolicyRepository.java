package ir.niopdc.policy.domain.policy;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends ListCrudRepository<Policy, Integer> {
}
