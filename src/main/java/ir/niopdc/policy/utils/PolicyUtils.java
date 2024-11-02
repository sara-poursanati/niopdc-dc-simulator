package ir.niopdc.policy.utils;

import ir.niopdc.policy.config.AppConfig;
import org.springframework.stereotype.Component;

@Component
public class PolicyUtils {

    private final AppConfig appConfig;

    public PolicyUtils(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public String getBlackListFileName(String versionId) {
        return appConfig.getBlackListPath()
                .concat(generateVersionName(versionId))
                .concat(appConfig.getBlackListSuffix());
    }

    public String generateVersionName(String versionId) {
        return appConfig.getBlackListPrefix().concat(versionId);
    }
}
