package ir.niopdc.simulator.domain.graylist;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrayListRepository extends ListCrudRepository<GrayList, String> {
}
