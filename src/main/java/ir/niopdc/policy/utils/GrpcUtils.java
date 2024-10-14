package ir.niopdc.policy.utils;

import ir.niopdc.common.grpc.policy.FuelRateDto;
import ir.niopdc.common.grpc.policy.PolicyMetadata;
import ir.niopdc.common.grpc.policy.FuelDto;
import ir.niopdc.common.grpc.policy.RateResponse;
import ir.niopdc.policy.domain.fuel.Fuel;
import ir.niopdc.policy.domain.fuelrate.FuelRate;

import java.util.ArrayList;
import java.util.List;

public class GrpcUtils {

    private GrpcUtils() {
    }

    public static RateResponse generateRateRequest(PolicyMetadata metadata, List<Fuel> fuels) {
        RateResponse.Builder builder = RateResponse.newBuilder();
        builder.setMetadata(metadata)
                .addAllFuels(getFuelDtos(fuels));
        return builder.build();
    }

    private static Iterable<? extends FuelDto> getFuelDtos(List<Fuel> fuels) {
        List<FuelDto> result = new ArrayList<>();
        for (Fuel fuel : fuels) {
            FuelDto fuelDto = FuelDto.newBuilder()
                    .setCode(fuel.getId())
                    .setName(fuel.getName())
                    .addAllRates(getFuelRateDtos(fuel))
                    .build();
            result.add(fuelDto);
        }
        return result;
    }

    private static Iterable<? extends FuelRateDto> getFuelRateDtos(Fuel fuel) {
        List<FuelRateDto> result = new ArrayList<>();
        for (FuelRate rate : fuel.getRates()) {
            FuelRateDto fuelRateDto = FuelRateDto.newBuilder()
                    .setRateNumber(rate.getRateNumber())
                    .setRateValue(rate.getRateValue())
                    .setFeeValue(rate.getFeeValue())
                    .build();
            result.add(fuelRateDto);
        }
        return result;
    }
}
