package ir.niopdc.policy.facade;

import ir.niopdc.common.entity.policy.*;
import ir.niopdc.common.grpc.policy.PolicyMetadata;
import ir.niopdc.common.grpc.policy.PolicyRequest;
import ir.niopdc.common.grpc.policy.RateResponse;
import ir.niopdc.common.grpc.policy.RegionalQuotaResponse;
import ir.niopdc.constant.ErrorMessages;
import ir.niopdc.policy.config.AppConfig;
import ir.niopdc.domain.blacklist.BlackList;
import ir.niopdc.domain.blacklist.BlackListService;
import ir.niopdc.domain.coding.CodingList;
import ir.niopdc.domain.coding.CodingListService;
import ir.niopdc.domain.fuel.Fuel;
import ir.niopdc.domain.fuel.FuelService;
import ir.niopdc.domain.fuelrate.FuelRate;
import ir.niopdc.domain.fuelrate.FuelRateService;
import ir.niopdc.domain.graylist.GrayList;
import ir.niopdc.domain.graylist.GrayListService;
import ir.niopdc.domain.policy.Policy;
import ir.niopdc.domain.policy.PolicyService;
import ir.niopdc.domain.policyversion.PolicyVersion;
import ir.niopdc.domain.policyversion.PolicyVersionKey;
import ir.niopdc.domain.policyversion.PolicyVersionService;
import ir.niopdc.domain.regionalquotarule.RegionalQuotaRule;
import ir.niopdc.domain.regionalquotarule.RegionalQuotaRuleService;
import ir.niopdc.policy.dto.FilePolicyResponseDto;
import ir.niopdc.policy.dto.ListResponseDto;
import ir.niopdc.policy.utils.GrpcUtils;
import ir.niopdc.policy.utils.PolicyUtils;
import ir.niopdc.policy.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
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
    private GrayListService grayListService;
    private PolicyUtils policyUtils;

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

    public FilePolicyResponseDto getCompleteBlackList() throws IOException {
        PolicyMetadata metadata = loadMetadataByVersion(PolicyEnum.BLACK_LIST);
        Path filePath = Path.of(policyUtils.getBlackListFileName(metadata.getVersion()));
        handleFileIntegrity(PolicyEnum.BLACK_LIST, filePath);
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

    public FilePolicyResponseDto getCompleteGrayList() throws IOException {
        PolicyMetadata metadata = loadMetadataByVersion(PolicyEnum.GRAY_LIST);
        Path filePath = Path.of(policyUtils.getGrayListFileName(metadata.getVersion()));
        handleFileIntegrity(PolicyEnum.GRAY_LIST, filePath);
        return getFilePolicyResponseDto(metadata, filePath);
    }

    public ListResponseDto getDifferentialGrayList(PolicyRequest request) {
        ZonedDateTime lastOperationTime = GrpcUtils.convertToZonedDateTime(request.getLastOperationDate());
        List<GrayList> grayLists = grayListService.findByOperationDateAfter(lastOperationTime);
        Optional<ZonedDateTime> maxOperationDateTime = grayLists
                .stream()
                .map(GrayList::getInsertionDateTime)
                .max(ZonedDateTime::compareTo);
        ZonedDateTime latestOperationDate = maxOperationDateTime.orElse(lastOperationTime);

        PolicyMetadata metadata = loadMetadataByOperationDate(PolicyEnum.GRAY_LIST, latestOperationDate);

        return getGrayListResponseDto(metadata, grayLists);
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
        Objects.requireNonNull(policy, String.format(ErrorMessages.POLICY_NOT_FOUND, policyEnum));
        PolicyVersionKey key = new PolicyVersionKey();
        key.setPolicyId(policy.getId());
        key.setVersion(policy.getCurrentVersion());
        PolicyVersion policyVersion = policyVersionService.findById(key);
        return loadMetadata(policyVersion);
    }

    private PolicyMetadata loadMetadataByOperationDate(PolicyEnum policyEnum, ZonedDateTime lastOperationDateTime) {
        Policy policy = policyService.findById(policyEnum.getValue());
        Objects.requireNonNull(policy, String.format(ErrorMessages.POLICY_NOT_FOUND, policyEnum));
        return loadMetadata(policy, lastOperationDateTime);
    }

    private String getHashOfCurrentVersionFile(PolicyEnum policyEnum) {
        Policy policy = policyService.findById(policyEnum.getValue());
        Objects.requireNonNull(policy, String.format(ErrorMessages.POLICY_NOT_FOUND, policyEnum));
        PolicyVersionKey key = PolicyVersionKey.builder().policyId(policy.getId()).version(policy.getCurrentVersion()).build();
        PolicyVersion policyVersion = policyVersionService.findById(key);
        return policyVersion.getChecksum();
    }

    private void handleFileIntegrity(PolicyEnum policyEnum, Path filePath) throws IOException {
        String originalHash = getHashOfCurrentVersionFile(policyEnum);
        String calculatedHash = SecurityUtils.createHash(filePath.toString());
        if (!originalHash.equals(calculatedHash)) {
            log.error("File integrity check failed for policy: {}. File: {}", policyEnum, filePath);
            throw new IllegalStateException("Hash mismatch detected: File integrity compromised.");
        }
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

    private ListResponseDto getGrayListResponseDto(PolicyMetadata metadata, List<GrayList> grayLists) {
        ListResponseDto response = new ListResponseDto();
        response.setMetadata(metadata);
        List<GrayListDto> grayListDtos = grayLists.stream().map(item ->
        {
            GrayListDto grayListDto = new GrayListDto();
            grayListDto.setCardId(item.getCardId());
            grayListDto.setReason(item.getReason());
            grayListDto.setType(item.getType());
            grayListDto.setOperation(OperationEnum.INSERT);
            return grayListDto;
        }).toList();
        response.setObjects(grayListDtos);
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

    @Autowired
    public void setGrayListService(GrayListService grayListService) {
        this.grayListService = grayListService;
    }

    @Autowired
    public void setPolicyUtils(PolicyUtils policyUtils) {
        this.policyUtils = policyUtils;
    }
}
