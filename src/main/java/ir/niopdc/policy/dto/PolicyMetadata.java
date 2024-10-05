package ir.niopdc.policy.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PolicyMetadata {
    private String policyId;
    private String version;
    private String versionName;
    private final List<String> csvList = new ArrayList<>();
}
