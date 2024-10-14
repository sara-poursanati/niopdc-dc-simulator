package ir.niopdc.policy.domain.graylist;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface GrayListRepository extends ListCrudRepository<GrayList, String> {
}
