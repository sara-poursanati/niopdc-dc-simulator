package ir.niopdc.policy.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ir.niopdc.common.entity.policy.*;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import ir.niopdc.policy.domain.fuel.Fuel;
import ir.niopdc.policy.domain.fuel.FuelService;
import ir.niopdc.policy.domain.fuelrate.FuelRate;
import ir.niopdc.policy.domain.quotarule.QuotaRule;
import ir.niopdc.policy.domain.quotarule.QuotaRuleService;
import ir.niopdc.policy.domain.regionalquotarule.RegionalQuotaRule;
import ir.niopdc.policy.domain.regionalquotarule.RegionalQuotaRuleService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PolicyFacade {
    private FuelService fuelService;
    private QuotaRuleService quotaRuleService;
    private RegionalQuotaRuleService regionalQuotaRuleService;
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
    public void setRegionalQuotaRuleService(RegionalQuotaRuleService regionalQuotaRuleService) {
        this.regionalQuotaRuleService = regionalQuotaRuleService;
    }

    @Autowired
    public void setBlackListService(BlackListService blackListService) {
        this.blackListService = blackListService;
    }

    @Autowired
    public void setObjectWriter(ObjectWriter objectWriter) {
        this.objectWriter = objectWriter;
    }

    @Transactional
    public PolicyDto getFuelRatePolicy() throws JsonProcessingException {
        PolicyDto response = new PolicyDto();

        loadMetadata(response);

        List<Fuel> fuels = fuelService.findAll();
        List<FuelDto> fuelDtos = fuels.stream().map(PolicyFacade::generateFuelDto).toList();

        generateContent(fuelDtos, response);

        return  response;
    }

    public PolicyDto getNationalQuotaPolicy() throws JsonProcessingException {
        PolicyDto response = new PolicyDto();

        loadMetadata(response);

        List<QuotaRule> quotaRules = quotaRuleService.findAll();
        List<NationalQuotaRuleDto> quotaRuleDtos = quotaRules.stream().map(PolicyFacade::generateQuotaRuleDto).toList();

        generateContent(quotaRuleDtos, response);

        return  response;

    }

    public PolicyDto getRegionalQuotaPolicy() throws JsonProcessingException {
        PolicyDto response = new PolicyDto();

        loadMetadata(response);

        List<RegionalQuotaRule> quotaRules = regionalQuotaRuleService.findAll();
        List<RegionalQuotaRuleDto> quotaRuleDtos = quotaRules.stream().map(PolicyFacade::generateRegionalQuotaRuleDto).toList();

        generateContent(quotaRuleDtos, response);

        return  response;
    }

//    public DataDto getBlackListPolicy() {
//        DataDto dto = new DataDto();
//        try (Stream<BlackList> blackListStream = blackListService.streamAll()) {
//            dto.setCsvList(blackListService.streamAll().map(csvUtils::convertToCsv));
//        }
//        return dto;
//    }
//
//    public PolicyResponse getGrayListPolicy() {
//        return null;
//    }
//
//    public PolicyResponse getCodingPolicy() {
//        return null;
//    }
//
//    public PolicyResponse getTerminalSoftware() {
//        return null;
//    }

    private PolicyMetadata getPolicyMetadata() {
        PolicyMetadata policyMetadata = new PolicyMetadata();
        policyMetadata.setPolicyId(Byte.parseByte(RandomStringUtils.random(2, false, true)));
        policyMetadata.setVersion(RandomStringUtils.random(10, false, true));
        policyMetadata.setVersionName(RandomStringUtils.random(20, true, true));
        return policyMetadata;
    }

    private static FuelDto generateFuelDto(Fuel fuel) {
        Objects.requireNonNull(fuel);
        FuelDto fuelDto = new FuelDto();
        fuelDto.setOperation(OperationEnum.INSERT);
        fuelDto.setId(fuel.getId());
        fuelDto.setName(fuel.getName());
        List<FuelRateDto> rateDtos = fuel.getRates().stream().map(PolicyFacade::generateRateDto).toList();
        fuelDto.setRates(rateDtos);
        return fuelDto;
    }

    private static FuelRateDto generateRateDto(FuelRate fuelRate) {
        Objects.requireNonNull(fuelRate);
        FuelRateDto fuelRateDto = new FuelRateDto();
        fuelRateDto.setOperation(OperationEnum.INSERT);
        BeanUtils.copyProperties(fuelRate, fuelRateDto);
        return fuelRateDto;
    }

    private static NationalQuotaRuleDto generateQuotaRuleDto(QuotaRule quotaRule) {
        Objects.requireNonNull(quotaRule);
        NationalQuotaRuleDto quotaRuleDto = new NationalQuotaRuleDto();
        quotaRuleDto.setOperation(OperationEnum.INSERT);
        BeanUtils.copyProperties(quotaRule, quotaRuleDto);
        return quotaRuleDto;
    }

    private static RegionalQuotaRuleDto generateRegionalQuotaRuleDto(RegionalQuotaRule regionalQuotaRule) {
        Objects.requireNonNull(regionalQuotaRule);
        RegionalQuotaRuleDto regionalQuotaRuleDto = new RegionalQuotaRuleDto();
        regionalQuotaRuleDto.setOperation(OperationEnum.INSERT);
        BeanUtils.copyProperties(regionalQuotaRule, regionalQuotaRuleDto);
        return regionalQuotaRuleDto;
    }

    private void generateContent(Object object, PolicyDto response) throws JsonProcessingException {
        String json = objectWriter.writeValueAsString(object);
        response.setJsonContent(json);
    }

    private void loadMetadata(PolicyDto response) {
        PolicyMetadata metadata = getPolicyMetadata();
        response.setMetadata(metadata);
    }
}
