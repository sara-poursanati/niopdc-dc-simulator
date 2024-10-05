package ir.niopdc.policy.facade;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ir.niopdc.common.entity.policy.FuelDto;
import ir.niopdc.common.entity.policy.RateDto;
import ir.niopdc.common.utils.CsvUtils;
import ir.niopdc.policy.domain.blacklist.BlackList;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import ir.niopdc.policy.domain.fuel.Fuel;
import ir.niopdc.policy.domain.fuel.FuelService;
import ir.niopdc.policy.domain.quotarule.QuotaRuleService;
import ir.niopdc.policy.dto.DataDto;
import ir.niopdc.policy.dto.PolicyMetadata;
import ir.niopdc.policy.dto.PolicyResponse;
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

    private CsvUtils csvUtils;

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
    public void setCsvUtils(CsvUtils csvUtils) {this.csvUtils = csvUtils;}

    public PolicyResponse getFuelRatePolicy() throws JsonProcessingException {
        PolicyMetadata metadata = getPolicyMetadata();
        List<Fuel> fuels = fuelService.findAll();
        List<FuelDto> fuelDtos = fuels.stream().map(PolicyFacade::generateFuelDto).toList();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(fuelDtos);

        PolicyResponse response = new PolicyResponse();
        response.setMetadata(metadata);
        response.setJsonContent(json);
        return  response;
    }

    public PolicyMetadata getQuotaPolicy() {
        PolicyMetadata policyMetadata = getPolicyMetadata();
        policyMetadata.getCsvList().addAll(quotaRuleService.findAll()
                .stream()
                .map(csvUtils::convertToCsv)
                .toList());
        return policyMetadata;
    }

    public PolicyMetadata getLocalQuotaPolicy() {
        PolicyMetadata policyMetadata = getPolicyMetadata();
        policyMetadata.getCsvList().addAll(quotaRuleService.findAll()
                .stream()
                .map(csvUtils::convertToCsv)
                .toList());
        return policyMetadata;
    }

    @Transactional(readOnly = true)
    public DataDto getBlackListPolicy() {
        DataDto dto = new DataDto();
        try (Stream<BlackList> blackListStream = blackListService.streamAll()) {
            dto.setCsvList(blackListService.streamAll().map(csvUtils::convertToCsv));
        }
        return dto;
    }

    public PolicyResponse getGrayListPolicy() {
        return null;
    }

    public PolicyResponse getCodingPolicy() {
        return null;
    }

    public PolicyResponse getTerminalSoftware() {
        return null;
    }

    private PolicyMetadata getPolicyMetadata() {
        PolicyMetadata policyMetadata = new PolicyMetadata();
        policyMetadata.setPolicyId(RandomStringUtils.random(3, true, true));
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
