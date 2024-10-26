package ir.niopdc.policy.domain.coding;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
interface CodingListRepository extends ListCrudRepository<CodingList, String> {
    List<CodingList> findByInsertionDateTimeAfter(ZonedDateTime lastOperationTime);
}
