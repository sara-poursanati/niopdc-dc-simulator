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
                .concat(getBlackListVersionName(versionId))
                .concat(appConfig.getBlackListSuffix());
    }

    public String getBlackListVersionName(String versionId) {
        return appConfig.getBlackListPrefix().concat(versionId);
    }

    public String getGrayListFileName(String versionId) {
        return appConfig.getGrayListPath()
                .concat(getGrayListVersionName(versionId))
                .concat(appConfig.getGrayListSuffix());
    }

    public String getGrayListVersionName(String versionId) {
        return appConfig.getBlackListPrefix().concat(versionId);
    }
}
