package ir.niopdc.simulator.tasks;

import ir.niopdc.simulator.blacklist.BlackList;
import ir.niopdc.simulator.blacklist.BlackListService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BlackListTask {

    private final Random rand = new Random();

    private final String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    private BlackListService blackListService;

    @Autowired
    public void setBlackListService(BlackListService blackListService) {
        this.blackListService = blackListService;
    }

    @Scheduled(cron = "${app.config.cron.black-list}")
    @Transactional
    public void RunBlackListTask() {

        String cardId = UUID.randomUUID().toString();

        BlackList blackList = new BlackList();

        blackList.setCardId(cardId);
        blackList.setReleaseTime(ZonedDateTime.now());
        blackList.setActiveTime(ZonedDateTime.now());
        blackList.setVersion(getRandomString());
        blackList.setReason(getRandomString());

        try {
            // save to DB
            blackListService.save(blackList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String getRandomString() {
        return SALTCHARS.substring(0, rand.nextInt(SALTCHARS.length()));
    }

}
