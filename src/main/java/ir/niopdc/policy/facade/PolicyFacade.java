package ir.niopdc.policy.facade;

import ir.niopdc.common.grpc.policy.PolicyMetadata;
import ir.niopdc.common.grpc.policy.RateResponse;
import ir.niopdc.common.grpc.policy.RegionalQuotaResponse;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import ir.niopdc.policy.domain.fuel.Fuel;
import ir.niopdc.policy.domain.fuel.FuelService;
import ir.niopdc.policy.domain.quotarule.QuotaRuleService;
import ir.niopdc.policy.domain.regionalquotarule.RegionalQuotaRule;
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

    @Value("${app.nationalQuota.path}")
    private String nationalQuotaPath;

    @Value("${app.terminalApp.path}")
    private String terminalAppPath;

    @Value("${app.blackList.path}")
    private String blackListPath;

    @Value("${app.codingList.path}")
    private String codingListPath;

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

    @Transactional
    public RateResponse getFuelRatePolicy() {
        PolicyMetadata metadata = loadMetadata();
        List<Fuel> fuels = fuelService.findAll();
        return GrpcUtils.generateRateResponse(metadata, fuels);
    }

    public FilePolicyResponseDto getNationalQuotaPolicy() {
        PolicyMetadata metadata = loadMetadata();
        Path filePath = Path.of(nationalQuotaPath);

        return getFilePolicyResponseDto(metadata, filePath);
    }

    public RegionalQuotaResponse getRegionalQuotaPolicy() {
        PolicyMetadata metadata = loadMetadata();
        List<RegionalQuotaRule> regionalQuotaRules = regionalQuotaRuleService.findAll();
        return GrpcUtils.generateRegionalQuotaResponse(metadata, regionalQuotaRules);
    }

    public FilePolicyResponseDto getBlackListPolicy() {
        PolicyMetadata metadata = loadMetadata();
        Path filePath = Path.of(blackListPath);

        return getFilePolicyResponseDto(metadata, filePath);
    }

    public FilePolicyResponseDto getCodingPolicy() {
        PolicyMetadata metadata = loadMetadata();
        Path filePath = Path.of(codingListPath);

        return getFilePolicyResponseDto(metadata, filePath);
    }

    public FilePolicyResponseDto getTerminalSoftware() {
        PolicyMetadata metadata = loadMetadata();
        Path filePath = Path.of(terminalAppPath);

        return getFilePolicyResponseDto(metadata, filePath);
    }

    private PolicyMetadata loadMetadata() {
        PolicyMetadata.Builder metadata = PolicyMetadata.newBuilder()
            .setPolicyId(Byte.parseByte(RandomStringUtils.random(2, false, true)))
            .setVersion(RandomStringUtils.random(10, false, true))
            .setVersionName(RandomStringUtils.random(20, true, true));
        return metadata.build();
    }

    private static FilePolicyResponseDto getFilePolicyResponseDto(PolicyMetadata metadata, Path filePath) {
        FilePolicyResponseDto response = new FilePolicyResponseDto();
        response.setMetadata(metadata);
        response.setFile(filePath);
        return response;
    }
}
