package ir.niopdc.simulator.domain.graylist;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrayListRepository extends ListCrudRepository<GrayList, String> {

    List<GrayList> findTop10By();
}
