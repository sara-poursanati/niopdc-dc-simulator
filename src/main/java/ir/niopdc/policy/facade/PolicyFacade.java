package ir.niopdc.policy.facade;

import ir.niopdc.common.entity.policy.PolicyEnum;
import ir.niopdc.common.grpc.policy.PolicyMetadata;
import ir.niopdc.common.grpc.policy.PolicyRequest;
import ir.niopdc.common.grpc.policy.RateResponse;
import ir.niopdc.common.grpc.policy.RegionalQuotaResponse;
import ir.niopdc.policy.domain.blacklist.BlackList;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import ir.niopdc.policy.domain.fuel.Fuel;
import ir.niopdc.policy.domain.fuel.FuelService;
import ir.niopdc.policy.domain.fuelrate.FuelRate;
import ir.niopdc.policy.domain.fuelrate.FuelRateService;
import ir.niopdc.policy.domain.policy.Policy;
import ir.niopdc.policy.domain.policy.PolicyService;
import ir.niopdc.policy.domain.policyversion.PolicyVersion;
import ir.niopdc.policy.domain.policyversion.PolicyVersionKey;
import ir.niopdc.policy.domain.policyversion.PolicyVersionService;
import ir.niopdc.policy.domain.regionalquotarule.RegionalQuotaRule;
import ir.niopdc.policy.domain.regionalquotarule.RegionalQuotaRuleService;
import ir.niopdc.policy.dto.FilePolicyResponseDto;
import ir.niopdc.policy.dto.ListResponseDto;
import ir.niopdc.policy.utils.GrpcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PolicyFacade {

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

    private FuelService fuelService;
    private RegionalQuotaRuleService regionalQuotaRuleService;
    private PolicyService policyService;
    private PolicyVersionService policyVersionService;
    private FuelRateService fuelRateService;
    private BlackListService blackListService;

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

    @Autowired
    public void setPolicyVersionService(PolicyVersionService policyVersionService) {
        this.policyVersionService = policyVersionService;
    }

    @Autowired
    public void setFuelRateService(FuelRateService fuelRateService) {
        this.fuelRateService = fuelRateService;
    }


    @Transactional
    public RateResponse getFuelRatePolicy(PolicyRequest request) {
        PolicyMetadata metadata = loadMetadataByVersion(PolicyEnum.RATE);
        if (isNotUpdated(request.getVersion(), metadata.getVersion())) {
            List<Fuel> fuels = fuelService.findAll();
            List<FuelRate> fuelRates = fuelRateService.findByVersion(metadata.getVersion());
            return GrpcUtils.generateRateResponse(metadata, fuels, fuelRates);
        } else {
            return GrpcUtils.generateRateResponse(metadata);
        }
    }

    public FilePolicyResponseDto getNationalQuotaPolicy(PolicyRequest request) {
        PolicyMetadata metadata = loadMetadataByVersion(PolicyEnum.NATIONAL_QUOTA);
        if (isNotUpdated(request.getVersion(), metadata.getVersion())) {
            Path filePath = Path.of(nationalQuotaPath);
            return getFilePolicyResponseDto(metadata, filePath);
        } else {
            return getFilePolicyResponseDto(metadata);
        }
    }

    public RegionalQuotaResponse getRegionalQuotaPolicy(PolicyRequest request) {
        PolicyMetadata metadata = loadMetadataByVersion(PolicyEnum.REGIONAL_QUOTA);
        if (isNotUpdated(request.getVersion(), metadata.getVersion())) {
            List<RegionalQuotaRule> regionalQuotaRules = regionalQuotaRuleService.findAll();
            return GrpcUtils.generateRegionalQuotaResponse(metadata, regionalQuotaRules);
        } else {
            return GrpcUtils.generateRegionalQuotaResponse(metadata);
        }

    }

    public FilePolicyResponseDto getCompleteBlackList() {
        PolicyMetadata metadata = loadMetadataByVersion(PolicyEnum.BLACK_LIST);
        Path filePath = Path.of(blackListPath);

        return getFilePolicyResponseDto(metadata, filePath);
    }

    public ListResponseDto getDifferentialBlackList(PolicyRequest request) {
        ZonedDateTime lastOperationTime = GrpcUtils.convertToZonedDateTime(request.getLastOperationDate());
        List<BlackList> blackLists = blackListService.findByOperationDateAfter(lastOperationTime);
        Optional<ZonedDateTime> maxOperationDateTime = blackLists
                .stream()
                .map(BlackList::getInsertionDateTime)
                .max(ZonedDateTime::compareTo);
        ZonedDateTime latestOperationDate = maxOperationDateTime.orElse(lastOperationTime);

        PolicyMetadata metadata = loadMetadataByOperationDate(PolicyEnum.BLACK_LIST, latestOperationDate);

        return getListResponseDto(metadata, blackLists);
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

    public FilePolicyResponseDto getTerminalSoftware(PolicyRequest request) {
        PolicyMetadata metadata = loadMetadataByVersion(PolicyEnum.APP);
        if (isNotUpdated(request.getVersion(), metadata.getVersion())) {
            Path filePath = Path.of(terminalAppPath);
            return getFilePolicyResponseDto(metadata, filePath);
        } else {
            return getFilePolicyResponseDto(metadata);
        }
    }

    private PolicyMetadata loadMetadataByVersion(PolicyEnum policyEnum) {
        Policy policy = policyService.findById(policyEnum.getValue());
        Objects.requireNonNull(policy, String.format("Policy not found for %s", policyEnum));
        PolicyVersionKey key = new PolicyVersionKey();
        key.setPolicyId(policy.getId());
        key.setVersion(policy.getCurrentVersion());
        PolicyVersion policyVersion = policyVersionService.findById(key);
        return loadMetadata(policyVersion);
    }

    private PolicyMetadata loadMetadataByOperationDate(PolicyEnum policyEnum, ZonedDateTime lastOperationDateTime) {
        Policy policy = policyService.findById(policyEnum.getValue());
        Objects.requireNonNull(policy, String.format("Policy not found for %s", policyEnum));
        return loadMetadata(policy, lastOperationDateTime);
    }

    private static boolean isNotUpdated(String clientVersion, String serverVersion) {
        return (clientVersion == null) || (!clientVersion.equals(serverVersion));
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

    private PolicyMetadata loadMetadata(Policy policy, ZonedDateTime lastOperationDateTime) {
        PolicyMetadata.Builder metadata = PolicyMetadata.newBuilder()
                .setPolicyId(policy.getId())
                .setOperationDateTime(GrpcUtils.convertToGoogleTimestamp(lastOperationDateTime));
        return metadata.build();
    }

    private static FilePolicyResponseDto getFilePolicyResponseDto(PolicyMetadata metadata, Path filePath) {
        FilePolicyResponseDto response = new FilePolicyResponseDto();
        response.setMetadata(metadata);
        response.setFile(filePath);
        return response;
    }

    private FilePolicyResponseDto getFilePolicyResponseDto(PolicyMetadata metadata) {
        FilePolicyResponseDto response = new FilePolicyResponseDto();
        response.setMetadata(metadata);
        return response;
    }

    private ListResponseDto getListResponseDto(PolicyMetadata metadata, List<?> objects) {
        ListResponseDto response = new ListResponseDto();
        response.setMetadata(metadata);
        response.setObjects(objects);
        return response;
    }

    @Autowired
    public void setBlackListService(BlackListService blackListService) {
        this.blackListService = blackListService;
    }
}
