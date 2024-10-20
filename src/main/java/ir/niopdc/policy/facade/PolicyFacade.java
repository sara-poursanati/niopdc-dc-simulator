package ir.niopdc.policy.facade;

import ir.niopdc.common.entity.policy.PolicyEnum;
import ir.niopdc.common.grpc.policy.PolicyMetadata;
import ir.niopdc.common.grpc.policy.PolicyRequest;
import ir.niopdc.common.grpc.policy.RateResponse;
import ir.niopdc.common.grpc.policy.RegionalQuotaResponse;
import ir.niopdc.policy.domain.fuel.Fuel;
import ir.niopdc.policy.domain.fuel.FuelService;
import ir.niopdc.policy.domain.policy.Policy;
import ir.niopdc.policy.domain.policy.PolicyService;
import ir.niopdc.policy.domain.policyversion.PolicyVersion;
import ir.niopdc.policy.domain.policyversion.PolicyVersionKey;
import ir.niopdc.policy.domain.policyversion.PolicyVersionService;
import ir.niopdc.policy.domain.regionalquotarule.RegionalQuotaRule;
import ir.niopdc.policy.domain.regionalquotarule.RegionalQuotaRuleService;
import ir.niopdc.policy.dto.FilePolicyResponseDto;
import ir.niopdc.policy.utils.GrpcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Service
public class PolicyFacade {
    private FuelService fuelService;
    private RegionalQuotaRuleService regionalQuotaRuleService;
    private PolicyService policyService;

    @Value("${app.nationalQuota.path}")
    private String nationalQuotaPath;

    @Value("${app.terminalApp.path}")
    private String terminalAppPath;

    @Value("${app.blackList.path}")
    private String blackListPath;

    @Value("${app.codingList.path}")
    private String codingListPath;

    @Value("${app.grayList.path}")
    private String grayListPath;
    private PolicyVersionService policyVersionService;

    @Autowired
    public void setFuelService(FuelService fuelService) {
        this.fuelService = fuelService;
    }

    @Autowired
    public void setRegionalQuotaRuleService(RegionalQuotaRuleService regionalQuotaRuleService) {
        this.regionalQuotaRuleService = regionalQuotaRuleService;
    }

    @Autowired
    public void setPolicyService(PolicyService policyService) {
        this.policyService = policyService;
    }


    @Transactional
    public RateResponse getFuelRatePolicy(PolicyRequest request) {
        PolicyMetadata metadata = loadMetadataByVersion(PolicyEnum.RATE, request.getVersion());
        List<Fuel> fuels = fuelService.findAll();
        return GrpcUtils.generateRateResponse(metadata, fuels);
    }

    public FilePolicyResponseDto getNationalQuotaPolicy() {
        PolicyMetadata metadata = loadMetadata(new PolicyVersion());
        Path filePath = Path.of(nationalQuotaPath);

        return getFilePolicyResponseDto(metadata, filePath);
    }

    public RegionalQuotaResponse getRegionalQuotaPolicy() {
        PolicyMetadata metadata = loadMetadata(new PolicyVersion());
        List<RegionalQuotaRule> regionalQuotaRules = regionalQuotaRuleService.findAll();
        return GrpcUtils.generateRegionalQuotaResponse(metadata, regionalQuotaRules);
    }

    public FilePolicyResponseDto getBlackListPolicy() {
        PolicyMetadata metadata = loadMetadata(new PolicyVersion());
        Path filePath = Path.of(blackListPath);

        return getFilePolicyResponseDto(metadata, filePath);
    }

    public FilePolicyResponseDto getCodingPolicy() {
        PolicyMetadata metadata = loadMetadata(new PolicyVersion());
        Path filePath = Path.of(codingListPath);

        return getFilePolicyResponseDto(metadata, filePath);
    }

    public FilePolicyResponseDto getGrayListPolicy() {
        PolicyMetadata metadata = loadMetadata(new PolicyVersion());
        Path filePath = Path.of(grayListPath);

        return getFilePolicyResponseDto(metadata, filePath);
    }

    public FilePolicyResponseDto getTerminalSoftware() {
        PolicyMetadata metadata = loadMetadata(new PolicyVersion());
        Path filePath = Path.of(terminalAppPath);

        return getFilePolicyResponseDto(metadata, filePath);
    }

    private PolicyMetadata loadMetadataByVersion(PolicyEnum policyEnum, String version) {
        Policy policy = policyService.findById(policyEnum.getValue());
        Objects.requireNonNull(policy, String.format("Policy not found for %s", policyEnum));
        if (!version.equals(policy.getCurrentVersion())) {
            PolicyVersionKey key = new PolicyVersionKey();
            key.setPolicyId(policy.getId());
            key.setVersion(policy.getCurrentVersion());
            PolicyVersion policyVersion = policyVersionService.findById(key);
            return loadMetadata(policyVersion);
        }
        return null;
    }

    private PolicyMetadata loadMetadata(PolicyVersion policyVersion) {
        PolicyMetadata.Builder metadata = PolicyMetadata.newBuilder()
                .setPolicyId(policyVersion.getId().getPolicyId())
                .setVersion(policyVersion.getId().getVersion())
                .setVersionName(policyVersion.getVersionName())
                .setOperationDateTime(GrpcUtils.convertToGoogleTimestamp(policyVersion.getReleaseTime()))
                .setActivationDateTime(GrpcUtils.convertToGoogleTimestamp(policyVersion.getActivationTime()));
        return metadata.build();
    }

    private static FilePolicyResponseDto getFilePolicyResponseDto(PolicyMetadata metadata, Path filePath) {
        FilePolicyResponseDto response = new FilePolicyResponseDto();
        response.setMetadata(metadata);
        response.setFile(filePath);
        return response;
    }

    @Autowired
    public void setPolicyVersionService(PolicyVersionService policyVersionService) {
        this.policyVersionService = policyVersionService;
    }
}
