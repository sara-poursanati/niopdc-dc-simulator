package ir.niopdc.policy.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import ir.niopdc.common.grpc.policy.FilePolicyResponse;
import ir.niopdc.common.grpc.policy.PolicyMetadata;
import ir.niopdc.common.grpc.policy.RateResponse;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import ir.niopdc.policy.domain.fuel.Fuel;
import ir.niopdc.policy.domain.fuel.FuelService;
import ir.niopdc.policy.domain.quotarule.QuotaRuleService;
import ir.niopdc.policy.domain.regionalquotarule.RegionalQuotaRuleService;
import ir.niopdc.policy.dto.FilePolicyResponseDto;
import ir.niopdc.policy.utils.GrpcUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.List;

@Service
public class PolicyFacade {
    private FuelService fuelService;
    private QuotaRuleService quotaRuleService;
    private RegionalQuotaRuleService regionalQuotaRuleService;
    private BlackListService blackListService;

    private ObjectWriter objectWriter;

    @Value("${app.nationalQuota.path}")
    private String nationalQuotaPath;

    @Value("${app.terminalApp.path}")
    private String terminalAppPath;

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
    public RateResponse getFuelRatePolicy() {
        PolicyMetadata metadata = loadMetadata();
        List<Fuel> fuels = fuelService.findAll();
        return GrpcUtils.generateRateRequest(metadata, fuels);
    }

    public FilePolicyResponseDto getNationalQuotaPolicy() {
        PolicyMetadata metadata = loadMetadata();
        Path filePath = Path.of(nationalQuotaPath);

        FilePolicyResponseDto response = new FilePolicyResponseDto();
        response.setMetadata(metadata);
        response.setFile(filePath);
        return response;
    }

//    public PolicyDto getRegionalQuotaPolicy() throws JsonProcessingException {
//        PolicyDto response = new PolicyDto();
//
//        loadMetadata(response);
//
//        List<RegionalQuotaRule> quotaRules = regionalQuotaRuleService.findAll();
//        List<RegionalQuotaRuleDto> quotaRuleDtos = quotaRules.stream().map(PolicyFacade::generateRegionalQuotaRuleDto).toList();
//
//        generateContent(quotaRuleDtos, response);
//
//        return  response;
//    }
//
////    public DataDto getBlackListPolicy() {
////        DataDto dto = new DataDto();
////        try (Stream<BlackList> blackListStream = blackListService.streamAll()) {
////            dto.setCsvList(blackListService.streamAll().map(csvUtils::convertToCsv));
////        }
////        return dto;
////    }
////
////    public PolicyResponse getGrayListPolicy() {
////        return null;
////    }
////
////    public PolicyResponse getCodingPolicy() {
////        return null;
////    }
////
    public FilePolicyResponseDto getTerminalSoftware() {
        PolicyMetadata metadata = loadMetadata();
        Path filePath = Path.of(terminalAppPath);

        FilePolicyResponseDto response = new FilePolicyResponseDto();
        response.setMetadata(metadata);
        response.setFile(filePath);
        return response;
    }
//
//    private PolicyMetadata getPolicyMetadata() {
//        PolicyMetadata policyMetadata = new PolicyMetadata();
//        policyMetadata.setPolicyId(Byte.parseByte(RandomStringUtils.random(2, false, true)));
//        policyMetadata.setVersion(RandomStringUtils.random(10, false, true));
//        policyMetadata.setVersionName(RandomStringUtils.random(20, true, true));
//        return policyMetadata;
//    }
//
//    private static FuelDto generateFuelDto(Fuel fuel) {
//        Objects.requireNonNull(fuel);
//        FuelDto fuelDto = new FuelDto();
//        fuelDto.setOperation(OperationEnum.INSERT);
//        fuelDto.setId(fuel.getId());
//        fuelDto.setName(fuel.getName());
//        List<FuelRateDto> rateDtos = fuel.getRates().stream().map(PolicyFacade::generateRateDto).toList();
//        fuelDto.setRates(rateDtos);
//        return fuelDto;
//    }
//
//    private static FuelRateDto generateRateDto(FuelRate fuelRate) {
//        Objects.requireNonNull(fuelRate);
//        FuelRateDto fuelRateDto = new FuelRateDto();
//        fuelRateDto.setOperation(OperationEnum.INSERT);
//        BeanUtils.copyProperties(fuelRate, fuelRateDto);
//        return fuelRateDto;
//    }
//
//    private static NationalQuotaRuleDto generateQuotaRuleDto(QuotaRule quotaRule) {
//        Objects.requireNonNull(quotaRule);
//        NationalQuotaRuleDto quotaRuleDto = new NationalQuotaRuleDto();
//        quotaRuleDto.setOperation(OperationEnum.INSERT);
//        BeanUtils.copyProperties(quotaRule, quotaRuleDto);
//        return quotaRuleDto;
//    }
//
//    private static RegionalQuotaRuleDto generateRegionalQuotaRuleDto(RegionalQuotaRule regionalQuotaRule) {
//        Objects.requireNonNull(regionalQuotaRule);
//        RegionalQuotaRuleDto regionalQuotaRuleDto = new RegionalQuotaRuleDto();
//        regionalQuotaRuleDto.setOperation(OperationEnum.INSERT);
//        BeanUtils.copyProperties(regionalQuotaRule, regionalQuotaRuleDto);
//        return regionalQuotaRuleDto;
//    }
//
//    private void generateContent(Object object, PolicyDto response) throws JsonProcessingException {
//        String json = objectWriter.writeValueAsString(object);
//        response.setJsonContent(json);
//    }

    private PolicyMetadata loadMetadata() {
        PolicyMetadata.Builder metadata = PolicyMetadata.newBuilder()
            .setPolicyId(Byte.parseByte(RandomStringUtils.random(2, false, true)))
            .setVersion(RandomStringUtils.random(10, false, true))
            .setVersionName(RandomStringUtils.random(20, true, true));
        return metadata.build();
    }
}
