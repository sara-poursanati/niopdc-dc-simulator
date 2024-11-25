package ir.niopdc.policy.job;

import ir.niopdc.common.entity.policy.PolicyEnum;
import ir.niopdc.common.grpc.policy.GrayListCardInfo;
import ir.niopdc.common.grpc.policy.GrayListResponse;
import ir.niopdc.common.grpc.policy.OperationEnumMessage;
import ir.niopdc.common.util.FileUtil;
import ir.niopdc.domain.graylist.GrayList;
import ir.niopdc.domain.graylist.GrayListService;
import ir.niopdc.domain.policy.Policy;
import ir.niopdc.domain.policy.PolicyService;
import ir.niopdc.domain.policyversion.PolicyVersion;
import ir.niopdc.domain.policyversion.PolicyVersionKey;
import ir.niopdc.domain.policyversion.PolicyVersionService;
import ir.niopdc.policy.utils.PolicyUtils;
import ir.niopdc.policy.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrayListExporter {

    private final GrayListService grayListService;
    private final PolicyVersionService policyVersionService;
    private final PolicyService policyService;
    private final PolicyUtils policyUtils;

    @Scheduled(cron = "${app.config.cron.gray-list}")
    @Transactional
    public void runExportTask() {
        log.info("Initializing grayList export");

        String newVersion = getNextPolicyVersion();
        String fileName = policyUtils.getGrayListFileName(newVersion);
        File grayListFile = new File(fileName);
        try {
            ZonedDateTime lastDateQuery = exportGrayList(grayListFile);
            String checksum = SecurityUtils.createSHA512Hash(grayListFile);
            processPolicyVersionUpdate(lastDateQuery, newVersion, checksum);
        } catch (Exception e) {
            handleFileCreationError(grayListFile, e);
        }
    }

    private ZonedDateTime exportGrayList(File file) throws IOException {
        List<GrayListCardInfo> grayListCardInfos = new ArrayList<>();
        ZonedDateTime currentDate = Instant.now().atZone(ZoneId.systemDefault());

        try (Stream<GrayList> grayListStream = grayListService.streamAllBeforeDate(currentDate)) {

            grayListStream.map(this::createGrayListCardInfo).forEach(grayListCardInfos::add);
            createFileFromGrayList(file, grayListCardInfos);

            log.info(
                    "Finished grayList export with {} records; last date of query: {}",
                    grayListCardInfos.size(),
                    currentDate);

        }
        return currentDate;
    }

    private void createFileFromGrayList(File file, List<GrayListCardInfo> grayListCardInfos)
            throws IOException {
        GrayListResponse response =
                GrayListResponse.newBuilder().addAllCardInfos(grayListCardInfos).build();
        FileUtil.createZipFile(file.getPath(), response.toByteArray());
    }

    private void processPolicyVersionUpdate(ZonedDateTime lastDate, String version, String checksum) {
        insertPolicyVersion(lastDate, version, checksum);
        updateCurrentPolicyVersion(version);
    }

    private void insertPolicyVersion(ZonedDateTime lastDate, String version, String checksum) {
        PolicyVersion policyVersion = buildPolicyVersion(lastDate, version, checksum);
        policyVersionService.save(policyVersion);
        log.info(
                "New policy version record inserted with versionName: {}", policyVersion.getVersionName());
    }

    private PolicyVersion buildPolicyVersion(
            ZonedDateTime lastDate, String version, String checksum) {
        PolicyVersionKey versionKey =
                PolicyVersionKey.builder()
                        .policyId(PolicyEnum.GRAY_LIST.getValue())
                        .version(version)
                        .build();

        return PolicyVersion.builder()
                .id(versionKey)
                .checksum(checksum)
                .versionName(policyUtils.getGrayListVersionName(version))
                .activationTime(lastDate)
                .releaseTime(lastDate)
                .build();
    }

    private void updateCurrentPolicyVersion(String version) {
        Policy policy = policyService.findById(PolicyEnum.GRAY_LIST.getValue());
        policy.setCurrentVersion(version);
        policyService.save(policy);
        log.info("Updated currentVersion in Policy table for policyId {}: {}", policy.getId(), version);
    }

    private String getNextPolicyVersion() {
        String currentVersion =
                policyService.findById(PolicyEnum.GRAY_LIST.getValue()).getCurrentVersion();
        return currentVersion != null ? String.valueOf(Integer.parseInt(currentVersion) + 1) : "1";
    }

    private GrayListCardInfo createGrayListCardInfo(GrayList grayList) {
        return GrayListCardInfo.newBuilder()
                .setCardId(grayList.getCardId())
                .setOperation(OperationEnumMessage.INSERT)
                .setType(grayList.getType())
                .setReason(grayList.getReason())
                .build();
    }

    private static void handleFileCreationError(File file, Exception e) {
        log.error("Error occurred after creating file, attempting to delete file: {}", file.getName(), e);
        FileUtils.deleteQuietly(file);
    }
}
