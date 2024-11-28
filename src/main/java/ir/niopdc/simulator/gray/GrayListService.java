package ir.niopdc.simulator.gray;

import com.github.javafaker.Faker;
import ir.niopdc.simulator.base.BaseService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
public class GrayListService extends BaseService<GrayListRepository, GrayList, String> {

    private final Faker faker = new Faker();

    public GrayList generateGrayList() {
        GrayList list = new GrayList();
        list.setCard_id(UUID.randomUUID().toString());
        list.setValide_id(UUID.randomUUID().toString());
        list.setReason(faker.lorem().sentence());
        list.setType(faker.options().option("TYPE_A", "TYPE_B", "TYPE_C"));
        list.setDate_time(Timestamp.from(Instant.now()));
        list.setUser_id(faker.idNumber().valid());

        return save(list);
    }
}
