package ir.niopdc.simulator.job;

import ir.niopdc.simulator.domain.grayhistory.GrayHistory;
import ir.niopdc.simulator.domain.grayhistory.GrayHistoryService;
import ir.niopdc.simulator.domain.graylist.GrayList;
import ir.niopdc.simulator.domain.graylist.GrayListService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Component
public class GrayListHistory {

    private final GrayHistoryService grayHistoryService;
    private final GrayListService grayListService;


    public GrayListHistory(GrayHistoryService grayHistoryService, GrayListService grayListService) {
        this.grayHistoryService = grayHistoryService;
        this.grayListService = grayListService;
    }

    @Scheduled(cron = "${app.config.cron.gray-history}")
    @Transactional
    public void removeGrayHistory() {
        List<GrayList> grayLists = grayListService.getRepository().findTop10By();
        for (GrayList grayList : grayLists) {
            GrayHistory grayHistory = new GrayHistory(
                    grayList.getCardId(),
                    grayList.getValideId(),
                    grayList.getReason(),
                    grayList.getType().toString(),
                    grayList.getUserId(),
                    new Timestamp(System.currentTimeMillis()),
                    "D"
            );
            grayHistoryService.save(grayHistory);
            grayListService.deleteById(grayList.getCardId());
        }
    }
}
