package ir.niopdc.policy.domain.whitelist;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhiteListRepository extends ListCrudRepository<WhiteList, String> {}
