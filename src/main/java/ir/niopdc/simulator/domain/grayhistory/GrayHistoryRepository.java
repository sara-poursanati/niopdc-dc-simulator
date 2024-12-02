package ir.niopdc.simulator.domain.grayhistory;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrayHistoryRepository extends ListCrudRepository<GrayHistory, String> {
}
