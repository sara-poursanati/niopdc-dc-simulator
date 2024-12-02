package ir.niopdc.simulator.blacklist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface BlackListRepository extends ListCrudRepository<BlackList, String> {

    @Query("select b from BlackList b")
    Stream<BlackList> streamAll();

    List<BlackList> findByReleaseTimeAfter(ZonedDateTime releaseDateTime);

    @Query("SELECT b FROM BlackList b WHERE b.releaseTime > :time ORDER BY b.releaseTime ASC")
    Page<BlackList> findAllByReleaseTimeAfter(@Param("time") ZonedDateTime time, Pageable pageable);
}
