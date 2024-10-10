package ir.niopdc.policy.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ir.niopdc.common.entity.policy.FuelDto;
import ir.niopdc.common.entity.policy.PolicyDto;
import ir.niopdc.common.entity.policy.PolicyMetadata;
import ir.niopdc.common.entity.policy.RateDto;
import ir.niopdc.common.utils.CsvUtils;
import ir.niopdc.policy.domain.blacklist.BlackList;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import ir.niopdc.policy.domain.fuel.Fuel;
import ir.niopdc.policy.domain.fuel.FuelService;
import ir.niopdc.policy.domain.quotarule.QuotaRule;
import ir.niopdc.policy.domain.quotarule.QuotaRuleService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class PolicyFacade {
    private FuelService fuelService;
    private QuotaRuleService quotaRuleService;
    private BlackListService blackListService;
    private ObjectWriter objectWriter;

    @Autowired
    public void setFuelService(FuelService fuelService) {
        this.fuelService = fuelService;
    }

    @Autowired
    public void setQuotaRuleService(QuotaRuleService quotaRuleService) {
        this.quotaRuleService = quotaRuleService;
    }

    @Autowired
    public void setBlackListService(BlackListService blackListService) {
        this.blackListService = blackListService;
    }

    @Autowired
    public void setObjectWriter(ObjectWriter objectWriter) {
        this.objectWriter = objectWriter;
    }

    public PolicyDto getFuelRatePolicy() throws JsonProcessingException {
        PolicyDto policy = new PolicyDto();

        PolicyMetadata metadata = getPolicyMetadata();
        policy.setMetadata(metadata);

        List<Fuel> fuels = fuelService.findAll();
        List<FuelDto> fuelDtos = fuels.stream().map(PolicyFacade::generateFuelDto).toList();
        String json = objectWriter.writeValueAsString(fuelDtos);
        policy.setAddedListJson(json);

        return  policy;
    }

    public PolicyDto getQuotaPolicy() throws JsonProcessingException {
        PolicyDto policy = new PolicyDto();

        PolicyMetadata metadata = getPolicyMetadata();
        policy.setMetadata(metadata);

        List<QuotaRule> quotaRules = quotaRuleService.findAll();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(quotaRules);
        policy.setAddedListJson(json);

        return policy;
    }

    public PolicyDto getLocalQuotaPolicy() throws JsonProcessingException {
        PolicyDto policy = new PolicyDto();

        PolicyMetadata metadata = getPolicyMetadata();
        policy.setMetadata(metadata);

        List<QuotaRule> quotaRules = quotaRuleService.findAll();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(quotaRules);
        policy.setAddedListJson(json);

        return policy;
    }

//    @Transactional(readOnly = true)
//    public DataDto getBlackListPolicy() {
//        DataDto dto = new DataDto();
//        try (Stream<BlackList> blackListStream = blackListService.streamAll()) {
//            dto.setCsvList(blackListService.streamAll().map(csvUtils::convertToCsv));
//        }
//        return dto;
//    }

    public PolicyDto getGrayListPolicy() {
        return null;
    }

    public PolicyDto getCodingPolicy() {
        return null;
    }

    public PolicyDto getTerminalSoftware() {
        return null;
    }

    private PolicyMetadata getPolicyMetadata() {
        PolicyMetadata policyMetadata = new PolicyMetadata();
        policyMetadata.setPolicyId(Byte.parseByte(RandomStringUtils.random(3, false, true)));
        policyMetadata.setVersion(RandomStringUtils.random(10, false, true));
        policyMetadata.setVersionName(RandomStringUtils.random(20, true, true));
        return policyMetadata;
    }

    private static FuelDto generateFuelDto(Fuel fuel) {
        Objects.requireNonNull(fuel);
        FuelDto fuelDto = new FuelDto();
        fuelDto.setFuelCode(fuel.getId());
        fuelDto.setName(fuel.getName());
        List<RateDto> rateDtos = new ArrayList<>();
        RateDto rate1Dto = new RateDto(1, fuel.getP());
        rateDtos.add(rate1Dto);
        RateDto rate2Dto = new RateDto(2, fuel.getP1());
        rateDtos.add(rate2Dto);
        RateDto rate3Dto = new RateDto(3, fuel.getP2());
        rateDtos.add(rate3Dto);
        RateDto rate4Dto = new RateDto(4, fuel.getP3());
        rateDtos.add(rate4Dto);
        return fuelDto;
    }
}
