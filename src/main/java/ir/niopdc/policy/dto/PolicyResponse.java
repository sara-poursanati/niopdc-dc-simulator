package ir.niopdc.policy.dto;

import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

@Data
public class PolicyResponse {
    private PolicyMetadata metadata;
    private String jsonContent;
    private List<String> csvContent;
    private Stream<String> streamContent;
}
