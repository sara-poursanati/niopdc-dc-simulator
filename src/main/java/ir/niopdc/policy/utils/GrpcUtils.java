package ir.niopdc.policy.utils;

import com.google.protobuf.Timestamp;
import ir.niopdc.common.grpc.policy.*;
import ir.niopdc.policy.domain.fuel.Fuel;
import ir.niopdc.policy.domain.fuelrate.FuelRate;
import ir.niopdc.policy.domain.regionalquotarule.RegionalQuotaRule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class GrpcUtils {

    private GrpcUtils() {
    }

    public static RateResponse generateRateResponse(PolicyMetadata metadata, List<Fuel> fuels) {
        RateResponse.Builder builder = RateResponse.newBuilder();
        builder.setMetadata(metadata)
                .addAllFuels(getFuelDtos(fuels));
        return builder.build();
    }

    public static RegionalQuotaResponse generateRegionalQuotaResponse(PolicyMetadata metadata, List<RegionalQuotaRule> regionalQuotaRules) {
        RegionalQuotaResponse.Builder builder = RegionalQuotaResponse.newBuilder();
        builder.setMetadata(metadata)
                .addAllRegionalQuotas(getRegionalQuotaDts(regionalQuotaRules));
        return builder.build();
    }

    private static Iterable<RegionalQuotaMessage> getRegionalQuotaDts(List<RegionalQuotaRule> regionalQuotaRules) {
        List<RegionalQuotaMessage> result = new ArrayList<>();
        for (RegionalQuotaRule rule : regionalQuotaRules) {
            RegionalQuotaMessage regionalQuotaMessage = RegionalQuotaMessage.newBuilder()
                    .setQuotaId(rule.getQuotaId())
                    .setCatId(rule.getId().getCatId())
                    .setDocId(rule.getDocId())
                    .setStationId(rule.getId().getStationId())
                    .setCf0(rule.getCf0())
                    .setCf1(rule.getCf1())
                    .setCf2(rule.getCf2())
                    .setLimitOneDay(rule.getLimitOneDay())
                    .setLimitOneDuration(rule.getLimitOneDuration())
                    .setLimitOneTime(rule.getLimitOneTime())
                    .setRoleId(rule.getRoleId())
                    .setSaving0(rule.getSaving0())
                    .setSaving1(rule.getSaving1())
                    .setMaxFuelCount(rule.getMaxFuelCount())
                    .setDuration(rule.getDuration())
                    .setOperation(operationEnum.INSERT)
                    .build();
            result.add(regionalQuotaMessage);
        }
        return result;
    }

    private static Iterable<FuelMessage> getFuelDtos(List<Fuel> fuels) {
        List<FuelMessage> result = new ArrayList<>();
        for (Fuel fuel : fuels) {
            FuelMessage fuelMessage = FuelMessage.newBuilder()
                    .setCode(fuel.getId())
                    .setName(fuel.getName())
                    .setOperation(operationEnum.INSERT)
                    .addAllRates(getFuelRateDtos(fuel))
                    .build();
            result.add(fuelMessage);
        }
        return result;
    }

    private static Iterable<FuelRateMessage> getFuelRateDtos(Fuel fuel) {
        List<FuelRateMessage> result = new ArrayList<>();
        for (FuelRate rate : fuel.getRates()) {
            FuelRateMessage fuelRateDto = FuelRateMessage.newBuilder()
                    .setRateNumber(rate.getRateNumber())
                    .setRateValue(rate.getRateValue())
                    .setFeeValue(rate.getFeeValue())
                    .build();
            result.add(fuelRateDto);
        }
        return result;
    }

    public static Timestamp convertToGoogleTimestamp(ZonedDateTime dateTime) {
        Instant instant = dateTime.toInstant();

        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

}
