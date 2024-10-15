package ir.niopdc.policy.dto;

import ir.niopdc.common.grpc.policy.PolicyMetadata;
import lombok.Data;

import java.nio.file.Path;

@Data
public class FilePolicyResponseDto {
    private PolicyMetadata metadata;
    private Path file;
}
