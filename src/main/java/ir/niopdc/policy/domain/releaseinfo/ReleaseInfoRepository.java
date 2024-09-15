package ir.niopdc.policy.domain.releaseinfo;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseInfoRepository extends ListCrudRepository<ReleaseInfo, ReleaseInfoKey> {
}
