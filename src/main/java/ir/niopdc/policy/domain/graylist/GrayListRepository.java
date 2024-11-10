package ir.niopdc.policy.domain.graylist;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
interface GrayListRepository extends ListCrudRepository<GrayList, String> {

    @Query("select g from GrayList g")
    Stream<GrayList> streamAll();

    @Query("select g from GrayList g where g.insertionDateTime <= :specifiedDate order by g.insertionDateTime asc")
    Stream<GrayList> streamAllBeforeDate(@Param("specifiedDate") ZonedDateTime specifiedDate);

    List<GrayList> findByInsertionDateTimeAfter(ZonedDateTime insertionDateTime);
}
