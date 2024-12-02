package ir.niopdc.simulator.scheduler;

import ir.niopdc.simulator.domain.grayList.GrayList;
import ir.niopdc.simulator.domain.grayList.GrayListService;
import ir.niopdc.simulator.domain.grayList.GrayListType;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

@Component
public class GrayListScheduler {

    private final GrayListService grayListService;
    private final Random random = new Random();

    public GrayListScheduler(GrayListService grayListService) {
        this.grayListService = grayListService;
    }

    @Scheduled(fixedRate = 100000)
    public void generateGrayList() {
        GrayList grayList = new GrayList();
        grayList.setCardId(UUID.randomUUID().toString());
        grayList.setValideId(UUID.randomUUID().toString());
        grayList.setReason(RandomStringUtils.randomAlphabetic(20));
        grayList.setType(GrayListType.values()[random.nextInt(GrayListType.values().length)]);
        grayList.setDateTime(ZonedDateTime.now());
        grayList.setUserId(RandomStringUtils.randomNumeric(10));

        grayListService.save(grayList);
        System.out.println("Gray Card created: " + grayList.getCardId());
    }
}

