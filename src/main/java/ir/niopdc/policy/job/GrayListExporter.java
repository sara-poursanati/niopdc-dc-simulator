package ir.niopdc.policy.job;

import ir.niopdc.common.entity.policy.PolicyEnum;
import ir.niopdc.common.grpc.policy.GrayListCardInfo;
import ir.niopdc.common.grpc.policy.GrayListResponse;
import ir.niopdc.common.grpc.policy.OperationEnumMessage;
import ir.niopdc.common.util.FileUtil;
import ir.niopdc.policy.config.AppConfig;
import ir.niopdc.policy.domain.graylist.GrayList;
import ir.niopdc.policy.domain.graylist.GrayListService;
import ir.niopdc.policy.domain.policy.Policy;
import ir.niopdc.policy.domain.policy.PolicyService;
import ir.niopdc.policy.domain.policyversion.PolicyVersion;
import ir.niopdc.policy.domain.policyversion.PolicyVersionKey;
import ir.niopdc.policy.domain.policyversion.PolicyVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class GrayListExporter {
    private final GrayListService grayListService;
    private final PolicyVersionService policyVersionService;
    private final PolicyService policyService;
    private final AppConfig appConfig;

    @Scheduled(cron = "${app.config.cron}")
    @Transactional
    public void runExportTask() {
        log.info("Initializing grayList export");

        String newVersion = getNextPolicyVersion();
        try {
            GrayList lastGrayListRecord = exportGrayList(createFilePath(newVersion));
            processPolicyVersionUpdate(lastGrayListRecord, newVersion);
        } catch (IOException e) {
            log.error("Failed to export grayList", e);
        }
    }

    private GrayList exportGrayList(String filePath) throws IOException {
        log.info("Starting grayList export");

        AtomicReference<GrayList> lastGrayListRecord = new AtomicReference<>();
        ConcurrentLinkedQueue<GrayListCardInfo> grayListCardInfos = new ConcurrentLinkedQueue<>();

        try (Stream<GrayList> grayListStream = grayListService.streamAll()) {
            grayListStream.forEach(
                    grayList -> {
                        grayListCardInfos.add(createGrayListCardInfo(grayList));
                        lastGrayListRecord.set(grayList);
                    });

            GrayListResponse response = GrayListResponse.newBuilder().addAllCardInfos(grayListCardInfos).build();
            FileUtil.createZipFile(filePath, response.toByteArray());

            log.info("Finishing grayList export with {} records", grayListCardInfos.size());
            log.info("lastGrayListRecord = {}", lastGrayListRecord);
        }
        return lastGrayListRecord.get();
    }

    private void processPolicyVersionUpdate(GrayList lastGrayListRecord, String version) {
        Objects.requireNonNull(lastGrayListRecord, "GrayList record cannot be null");

        insertPolicyVersion(lastGrayListRecord, version);
        updatePolicyCurrentVersion(version);
    }

    private void insertPolicyVersion(GrayList lastGrayListRecord, String newVersion) {
        PolicyVersionKey versionKey = PolicyVersionKey.builder()
                .policyId(PolicyEnum.GRAY_LIST.getValue())
                .version(newVersion)
                .build();

        PolicyVersion policyVersion = PolicyVersion.builder()
                .id(versionKey)
                .activationTime(lastGrayListRecord.getInsertionDateTime())
                .releaseTime(lastGrayListRecord.getInsertionDateTime())
                .versionName(generateVersionName(newVersion))
                .build();

        policyVersionService.save(policyVersion);
        log.info("New policy version record inserted with versionName: {}", generateVersionName(newVersion));
    }

    private void updatePolicyCurrentVersion(String version) {
        Policy policy = policyService.findById(PolicyEnum.GRAY_LIST.getValue());
        policy.setCurrentVersion(version);
        policyService.save(policy);
        log.info("Updated currentVersion in Policy table for policyId {}: {}", policy.getId(), version);
    }

    private String getNextPolicyVersion() {
        String currentVersion = policyService.findById(PolicyEnum.GRAY_LIST.getValue()).getCurrentVersion();
        return String.valueOf(Integer.parseInt(currentVersion) + 1);
    }

    private static GrayListCardInfo createGrayListCardInfo(GrayList grayList) {
    return GrayListCardInfo.newBuilder()
        .setCardId(grayList.getCardId())
        .setOperation(OperationEnumMessage.INSERT)
        .setType(grayList.getType())
        .setReason(grayList.getReason())
        .build();
    }

    private String createFilePath(String versionName) {
        return appConfig.getGrayListPath()
                .concat(appConfig.getGrayListPrefix())
                .concat(versionName)
                .concat(appConfig.getGrayListSuffix());
    }

    private String generateVersionName(String version) {
        return appConfig.getGrayListPrefix().concat(version);
    }
}

