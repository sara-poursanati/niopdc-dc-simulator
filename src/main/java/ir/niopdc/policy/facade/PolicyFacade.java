package ir.niopdc.policy.facade;

import ir.niopdc.common.entity.policy.BlackListDto;
import ir.niopdc.common.entity.policy.CodingDto;
import ir.niopdc.common.entity.policy.OperationEnum;
import ir.niopdc.common.entity.policy.PolicyEnum;
import ir.niopdc.common.grpc.policy.PolicyMetadata;
import ir.niopdc.common.grpc.policy.PolicyRequest;
import ir.niopdc.common.grpc.policy.RateResponse;
import ir.niopdc.common.grpc.policy.RegionalQuotaResponse;
import ir.niopdc.policy.config.AppConfig;
import ir.niopdc.policy.domain.blacklist.BlackList;
import ir.niopdc.policy.domain.blacklist.BlackListService;
import ir.niopdc.policy.domain.coding.CodingList;
import ir.niopdc.policy.domain.coding.CodingListService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PolicyFacade {
    private AppConfig appConfig;
    private FuelService fuelService;
    private RegionalQuotaRuleService regionalQuotaRuleService;
    private PolicyService policyService;
    private PolicyVersionService policyVersionService;
    private FuelRateService fuelRateService;
    private BlackListService blackListService;
    private CodingListService codingListService;

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
            Path filePath = Path.of(appConfig.getNationalQuotaPath());
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
        Path filePath = Path.of(appConfig.getBlackListPath());

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

        return getBlackListResponseDto(metadata, blackLists);
    }

    public FilePolicyResponseDto getCompleteCodingList() {
        PolicyMetadata metadata = loadMetadataByVersion(PolicyEnum.CODING);
        Path filePath = Path.of(appConfig.getCodingListPath());

        return getFilePolicyResponseDto(metadata, filePath);
    }

    public ListResponseDto getDifferentialCodingList(PolicyRequest request) {
        ZonedDateTime lastOperationTime = GrpcUtils.convertToZonedDateTime(request.getLastOperationDate());
        List<CodingList> codingLists = codingListService.findByOperationDateAfter(lastOperationTime);
        Optional<ZonedDateTime> maxOperationDateTime = codingLists
                .stream()
                .map(CodingList::getInsertionDateTime)
                .max(ZonedDateTime::compareTo);
        ZonedDateTime latestOperationDate = maxOperationDateTime.orElse(lastOperationTime);

        PolicyMetadata metadata = loadMetadataByOperationDate(PolicyEnum.CODING, latestOperationDate);

        return getCodingListResponseDto(metadata, codingLists);
    }

    public FilePolicyResponseDto getGrayListPolicy() {
        PolicyMetadata metadata = loadMetadataByVersion(PolicyEnum.GRAY_LIST);
        Path filePath = Path.of(appConfig.getGrayListPath());

        return getFilePolicyResponseDto(metadata, filePath);
    }

    public FilePolicyResponseDto getTerminalSoftware(PolicyRequest request) {
        PolicyMetadata metadata = loadMetadataByVersion(PolicyEnum.APP);
        if (isNotUpdated(request.getVersion(), metadata.getVersion())) {
            Path filePath = Path.of(appConfig.getTerminalAppPath());
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

    private ListResponseDto getBlackListResponseDto(PolicyMetadata metadata, List<BlackList> blackLists) {
        ListResponseDto response = new ListResponseDto();
        response.setMetadata(metadata);
        List<BlackListDto> blackListDtos = blackLists.stream().map(item ->
        {
            BlackListDto blackListDto = new BlackListDto();
            blackListDto.setCardId(item.getCardId());
            blackListDto.setOperation(OperationEnum.INSERT);
            return blackListDto;
        }).toList();
        response.setObjects(blackListDtos);
        return response;
    }

    private ListResponseDto getCodingListResponseDto(PolicyMetadata metadata, List<CodingList> codingLists) {
        ListResponseDto response = new ListResponseDto();
        response.setMetadata(metadata);
        List<CodingDto> codingDtos = codingLists.stream().map(item ->
        {
            CodingDto codingDto = new CodingDto();
            codingDto.setCardId(item.getCardId());
            codingDto.setOperation(OperationEnum.INSERT);
            return codingDto;
        }).toList();
        response.setObjects(codingDtos);
        return response;
    }

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

    @Autowired
    public void setBlackListService(BlackListService blackListService) {
        this.blackListService = blackListService;
    }

    @Autowired
    public void setCodingListService(CodingListService codingListService) {
        this.codingListService = codingListService;
    }

    @Autowired
    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }
}
