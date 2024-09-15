package ir.niopdc.policy.domain.releaseinfo;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "RELEASE_INFO")
public class ReleaseInfo {

    @EmbeddedId
    private ReleaseInfoKey id;
    @Column(name = "VER_NAME")
    private String versionName;
    private LocalDateTime releaseTime;
    private LocalDateTime activeTime;
    private String state;
    private String operatorId;
    @Column(name = "OPTIME")
    private LocalDateTime operationTime;

    public ReleaseInfoKey getId() {
        return id;
    }

    public void setId(ReleaseInfoKey id) {
        this.id = id;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public LocalDateTime getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(LocalDateTime releaseTime) {
        this.releaseTime = releaseTime;
    }

    public LocalDateTime getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(LocalDateTime activeTime) {
        this.activeTime = activeTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }
}
