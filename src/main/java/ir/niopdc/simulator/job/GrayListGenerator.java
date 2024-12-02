package ir.niopdc.simulator.job;

import ir.niopdc.simulator.domain.graylist.GrayList;
import ir.niopdc.simulator.domain.graylist.GrayListService;
import ir.niopdc.simulator.domain.graylist.GrayListType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
public class GrayListGenerator {

    private final GrayListService grayListService;
    private final Random random = new Random();

    public GrayListGenerator(GrayListService grayListService) {
        this.grayListService = grayListService;
    }

    @Scheduled(cron = "${app.config.cron.gray-list})")
    public void generateGrayList() {
        GrayList grayList = new GrayList();
        grayList.setCardId(UUID.randomUUID().toString());
        grayList.setValideId(UUID.randomUUID().toString());
        grayList.setReason(RandomStringUtils.randomAlphabetic(20));
        grayList.setType(GrayListType.values()[random.nextInt(GrayListType.values().length)]);
        grayList.setDateTime(ZonedDateTime.now());
        grayList.setUserId(RandomStringUtils.randomNumeric(10));

        grayListService.save(grayList);
        log.info("Gray Card created: {}", grayList.getCardId());
    }
}

