package ir.niopdc.policy.domain.blacklist;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
interface BlackListRepository extends ListCrudRepository<BlackList, String> {

    @Query("select b from BlackList b")
    Stream<BlackList> streamAll();

    List<BlackList> findByInsertionDateTimeAfter(ZonedDateTime insertionDateTime);
}
