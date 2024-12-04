package ir.niopdc.simulator.domain.graylist;

import ir.niopdc.common.entity.base.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrayListService extends BaseService<GrayListRepository, GrayList, String> {

    public List<GrayList> findTop10() {
        return getRepository().findTop10By();
    }
}
