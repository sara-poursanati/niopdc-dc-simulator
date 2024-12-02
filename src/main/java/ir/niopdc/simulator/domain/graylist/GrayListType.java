package ir.niopdc.simulator.domain.graylist;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GrayListType {
    ERROR("01"),
    WARNING("02");

    private final String value;
}
