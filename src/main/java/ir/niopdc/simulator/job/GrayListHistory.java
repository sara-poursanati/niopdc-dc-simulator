package ir.niopdc.simulator.job;

import ir.niopdc.simulator.domain.grayhistory.GrayHistory;
import ir.niopdc.simulator.domain.grayhistory.GrayHistoryService;
import ir.niopdc.simulator.domain.graylist.GrayList;
import ir.niopdc.simulator.domain.graylist.GrayListService;
import jakarta.persistence.PreRemove;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GrayListHistory {

    private final GrayHistoryService grayHistoryService;
    private final GrayListService grayListService;

    public GrayListHistory(GrayHistoryService grayHistoryService, GrayListService grayListService) {
        this.grayHistoryService = grayHistoryService;
        this.grayListService = grayListService;
    }

    public void removeFromGrayList(String cardId) {
        Optional<GrayList> grayList = grayListService.getRepository().findById(cardId);
        grayList.ifPresent(list -> grayListService.deleteById(cardId));
    }


    @PreRemove
    public void removeGrayHistory(GrayList grayList) {
        GrayHistory grayHistory = new GrayHistory(
                grayList.getCardId(),
                grayList.getValideId(),
                grayList.getReason(),
                grayList.getType().toString(),
                grayList.getUserId(),
                new java.sql.Timestamp(System.currentTimeMillis()),
                "D"
        );

        grayHistoryService.save(grayHistory);
    }
}
