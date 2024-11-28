package ir.niopdc.simulator.scheduler;

import ir.niopdc.simulator.gray.GrayList;
import ir.niopdc.simulator.gray.GrayListService;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

@Component
public class GrayListScheduler {

    private final GrayListService grayListService;

    public GrayListScheduler(GrayListService grayListService) {
        this.grayListService = grayListService;
    }

    @Scheduled(fixedRate = 100000)
    public void generateGrayCard() {
        GrayList grayCard = grayListService.generateGrayList();
        System.out.println("Gray Card created: " + grayCard.getCard_id());
    }
}

