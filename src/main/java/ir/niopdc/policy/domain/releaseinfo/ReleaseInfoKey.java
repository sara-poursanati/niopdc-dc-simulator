package ir.niopdc.policy.domain.releaseinfo;

import jakarta.persistence.Column;

public class ReleaseInfoKey {

    private String releaseInfo;
    @Column(name = "VER")
    private String version;

    public String getReleaseInfo() {
        return releaseInfo;
    }

    public void setReleaseInfo(String releaseInfo) {
        this.releaseInfo = releaseInfo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReleaseInfoKey that = (ReleaseInfoKey) o;
        return releaseInfo.equals(that.releaseInfo) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        int result = releaseInfo.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }
}
