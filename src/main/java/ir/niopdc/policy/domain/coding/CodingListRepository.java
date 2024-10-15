package ir.niopdc.policy.domain.coding;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CodingListRepository extends ListCrudRepository<CodingList, String> {
}
