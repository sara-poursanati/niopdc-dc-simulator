package ir.niopdc.policy.domain.blacklist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Repository
interface BlackListRepository extends ListCrudRepository<BlackList, String> {

    @Query("select b from BlackList b")
    Stream<BlackList> streamAll();

    @Query("SELECT b FROM BlackList b WHERE b.insertionDateTime > :time ORDER BY b.insertionDateTime ASC")
    Page<BlackList> findAllByInsertionDateTimeAfter(@Param("time") LocalDateTime time, Pageable pageable);
}
