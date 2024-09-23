package ir.niopdc.policy.domain.blacklist;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends ListCrudRepository<BlackList, String> {
}
