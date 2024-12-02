package ir.niopdc.simulator.domain.blacklist;

import ir.niopdc.simulator.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class BlackListService extends BaseService<BlackListRepository, BlackList, String> {

    @Transactional(readOnly = true)
    public Page<BlackList> fetchPageAfter(ZonedDateTime time, Pageable page) {
        return getRepository().findAllByReleaseTimeAfter(time, page);
    }

    @Transactional(readOnly = true)
    public Stream<BlackList> streamAll() {
        return getRepository().streamAll();
    }

    public List<BlackList> findByOperationDateAfter(ZonedDateTime lastOperationTime) {
        return getRepository().findByReleaseTimeAfter(lastOperationTime);
    }
}
