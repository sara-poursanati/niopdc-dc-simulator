package ir.niopdc.simulator.job;

import ir.niopdc.simulator.domain.blacklist.BlackList;
import ir.niopdc.simulator.domain.blacklist.BlackListService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BlackListTask {

    private BlackListService blackListService;

    @Autowired
    public void setBlackListService(BlackListService blackListService) {
        this.blackListService = blackListService;
    }

    @Scheduled(cron = "${app.config.cron.black-list}")
    @Transactional
    public void runBlackListTask() {

        String cardId = UUID.randomUUID().toString();

        BlackList blackList = new BlackList();

        blackList.setCardId(cardId);
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        blackList.setReleaseTime(currentDateTime);
        blackList.setActiveTime(currentDateTime);
        blackList.setVersion(getRandomString());
        blackList.setReason(getRandomString());

        // save to DB
        blackListService.save(blackList);
    }

    private String getRandomString() {
        return RandomStringUtils.randomAlphanumeric(5);
    }

}
