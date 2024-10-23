package ir.niopdc.policy.dto;

import ir.niopdc.common.entity.policy.BlackListDto;
import ir.niopdc.common.grpc.policy.PolicyMetadata;
import lombok.Data;

import java.util.List;

@Data
public class BlackListResponseDto {
    private PolicyMetadata metadata;
    private List<BlackListDto> blackListDtos;
}
