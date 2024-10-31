package ir.niopdc.policy.domain.graylist;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
interface GrayListRepository extends ListCrudRepository<GrayList, String> {

    @Query("select g from GrayList g")
    Stream<GrayList> streamAll();

    List<GrayList> findByInsertionDateTimeAfter(ZonedDateTime insertionDateTime);
}
