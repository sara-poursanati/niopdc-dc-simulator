package ir.niopdc.policy.domain.graylist;

import ir.niopdc.base.BaseService;
import ir.niopdc.policy.domain.blacklist.BlackList;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class GrayListService extends BaseService<GrayListRepository, GrayList, String> {

    public List<GrayList> findByOperationDateAfter(ZonedDateTime lastOperationTime) {
        return getRepository().findByInsertionDateTimeAfter(lastOperationTime);
    }
}
