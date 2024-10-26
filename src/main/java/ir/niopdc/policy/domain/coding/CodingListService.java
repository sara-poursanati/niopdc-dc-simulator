package ir.niopdc.policy.domain.coding;

import ir.niopdc.base.BaseService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class CodingListService extends BaseService<CodingListRepository, CodingList, String> {
    public List<CodingList> findByOperationDateAfter(ZonedDateTime lastOperationTime) {
        return getRepository().findByInsertionDateTimeAfter(lastOperationTime);
    }
}
