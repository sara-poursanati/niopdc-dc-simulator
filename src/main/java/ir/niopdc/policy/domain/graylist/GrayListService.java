package ir.niopdc.policy.domain.graylist;

import ir.niopdc.base.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class GrayListService extends BaseService<GrayListRepository, GrayList, String> {

  @Transactional(readOnly = true)
  public Stream<GrayList> streamAll() {
    return getRepository().streamAll();
  }

  @Transactional(readOnly = true)
  public Stream<GrayList> streamAllBeforeDate(ZonedDateTime date) {
    return getRepository().streamAllBeforeDate(date);
  }

  public List<GrayList> findByOperationDateAfter(ZonedDateTime lastOperationTime) {
    return getRepository().findByInsertionDateTimeAfter(lastOperationTime);
  }
}