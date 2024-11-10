package ir.niopdc.policy.domain.blacklist;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
interface BlackListRepository extends ListCrudRepository<BlackList, String> {

    @Query("select b from BlackList b")
    Stream<BlackList> streamAll();

    @Query("select b from BlackList b left join WhiteList w on b.cardId = w.cardId where w.cardId is null")
    Stream<BlackList> streamBlackListMinusWhiteList();

    @Query("select b from BlackList b where b.insertionDateTime <= :specifiedDate order by b.insertionDateTime asc")
    Stream<BlackList> streamAllBeforeDate(@Param("specifiedDate") ZonedDateTime specifiedDate);

    @Query("SELECT b FROM BlackList b LEFT JOIN WhiteList w ON b.cardId = w.cardId " +
            "WHERE w.cardId IS NULL AND b.insertionDateTime <= :specifiedDate " +
            "ORDER BY b.insertionDateTime ASC")
    Stream<BlackList> streamBlackListMinusWhiteListBeforeDate(@Param("specifiedDate") ZonedDateTime specifiedDate);

    List<BlackList> findByInsertionDateTimeAfter(ZonedDateTime insertionDateTime);
}
