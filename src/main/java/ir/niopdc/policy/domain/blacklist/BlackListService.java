package ir.niopdc.policy.domain.blacklist;

import ir.niopdc.base.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
public class BlackListService extends BaseService<BlackListRepository, BlackList, String> {

    @Transactional(readOnly = true)
    public Stream<BlackList> streamAll() {
        return getRepository().streamAll();
    }
}
