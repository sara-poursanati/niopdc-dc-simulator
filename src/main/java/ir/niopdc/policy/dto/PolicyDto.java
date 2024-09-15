package ir.niopdc.policy.dto;

import java.util.ArrayList;
import java.util.List;

public class PolicyDto {
    private String policyId;
    private String version;
    private String versionName;
    private final List<String> csvList = new ArrayList<>();

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public List<String> getCsvList() {
        return csvList;
    }
}
