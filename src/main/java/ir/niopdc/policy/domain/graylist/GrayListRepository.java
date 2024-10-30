package ir.niopdc.policy.domain.graylist;

import ir.niopdc.policy.domain.blacklist.BlackList;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
interface GrayListRepository extends ListCrudRepository<GrayList, String> {

    List<GrayList> findByInsertionDateTimeAfter(ZonedDateTime insertionDateTime);
}
