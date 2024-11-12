package ir.niopdc.policy.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Builder
@Getter
public class PolicyVersionDto {
    private String version;
    private String checksum;
    private ZonedDateTime lastQueryDate;
}
