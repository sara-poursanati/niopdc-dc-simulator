package ir.niopdc.policy.dto;

import lombok.Data;

import java.util.stream.Stream;

@Data
public class DataDto {

    private String policyId;
    private String version;
    private String versionName;
    private Stream<String> csvList;
}
