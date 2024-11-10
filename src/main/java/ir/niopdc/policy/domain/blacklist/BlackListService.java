package ir.niopdc.policy.domain.blacklist;

import ir.niopdc.base.BaseService;
import ir.niopdc.policy.domain.graylist.GrayList;
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
    public Stream<BlackList> streamAll() {
        return getRepository().streamAll();
    }

    @Transactional(readOnly = true)
    public Stream<BlackList> streamAllMinusWhiteList() {
        return getRepository().streamBlackListMinusWhiteList();
    }

    @Transactional(readOnly = true)
    public Stream<BlackList> streamAllBeforeDate(ZonedDateTime date) {
        return getRepository().streamAllBeforeDate(date);
    }

    @Transactional(readOnly = true)
    public Stream<BlackList> streamBlackListMinusWhiteListBeforeDate(ZonedDateTime date) {
        return getRepository().streamBlackListMinusWhiteListBeforeDate(date);
    }

    public List<BlackList> findByOperationDateAfter(ZonedDateTime lastOperationTime) {

        return getRepository().findByInsertionDateTimeAfter(lastOperationTime);
    }
}
