package ir.niopdc.policy.dto;

import ir.niopdc.common.entity.policy.BlackListDto;
import ir.niopdc.common.grpc.policy.PolicyMetadata;
import lombok.Data;

import java.util.List;

@Data
public class ListResponseDto {
    private PolicyMetadata metadata;
    private List<?> objects;
}
