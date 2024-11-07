package ir.niopdc.policy.facade;

import ir.niopdc.common.grpc.profile.ProfileRequest;
import ir.niopdc.common.grpc.profile.ProfileResponse;
import ir.niopdc.common.grpc.profile.ProfileTopicPolicy;
import ir.niopdc.domain.fuelstation.FuelStation;
import ir.niopdc.domain.fuelstationpolicy.FuelStationPolicy;
import ir.niopdc.domain.fuelstationpolicy.FuelStationPolicyService;
import ir.niopdc.domain.fuelterminal.FuelTerminalService;
import ir.niopdc.domain.mediagateway.MediaGateway;
import ir.niopdc.domain.mediagateway.MediaGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileFacade {

    private MediaGatewayService mediaGatewayService;
    private FuelStationPolicyService fuelStationPolicyService;
    private FuelTerminalService fuelTerminalService;

    @Autowired
    private void setMediaGatewayService(MediaGatewayService mediaGatewayService) {
        this.mediaGatewayService = mediaGatewayService;
    }

    @Autowired
    private void setFuelStationPolicyService(FuelStationPolicyService fuelStationPolicyService) {
        this.fuelStationPolicyService = fuelStationPolicyService;
    }

    @Autowired
    private void setFuelTerminalService(FuelTerminalService fuelTerminalService) {
        this.fuelTerminalService = fuelTerminalService;
    }

    public ProfileResponse getProfile(ProfileRequest request) {
        MediaGateway mediaGateway = mediaGatewayService.findById(request.getMediaGatewayId());
        if (mediaGateway == null) {
            throw new IllegalArgumentException("No such media gateway");
        }
        return buildProfileResponse(mediaGateway);
    }

    private ProfileResponse buildProfileResponse(MediaGateway mediaGateway) {
        FuelStation fuelStation = mediaGateway.getFuelStation();
        List<FuelStationPolicy> fuelStationPolicies = fuelStationPolicyService.findByFuelStationId(fuelStation.getId());
        ProfileResponse.Builder builder =  ProfileResponse.newBuilder();
        builder.setAddress(fuelStation.getAddress())
                .setAreaId(fuelStation.getAreaId())
                .setName(fuelStation.getName())
                .setMediaGatewayId(mediaGateway.getSerialNumber())
                .setFuelStationId(fuelStation.getId())
                .setTerminalCount((int)fuelTerminalService.getPtCountByFuelStation(fuelStation.getId()))
                .setZoneId(fuelStation.getZoneId())
                .addAllTopicPolicies(getProfileTopicPolicies(fuelStationPolicies));
        return builder.build();
    }

    private List<ProfileTopicPolicy> getProfileTopicPolicies(List<FuelStationPolicy> fuelStationPolicies) {
        List<ProfileTopicPolicy> profileTopicPolicies = new ArrayList<>();
        for (FuelStationPolicy policyModel : fuelStationPolicies) {
            addTopicPolicy(policyModel, profileTopicPolicies);
        }
        return profileTopicPolicies;
    }

    private static void addTopicPolicy(FuelStationPolicy policyModel, List<ProfileTopicPolicy> profileTopicPolicies) {
        profileTopicPolicies.add(ProfileTopicPolicy
                .newBuilder()
                .setBigDelay(policyModel.getBigDelay())
                .setPolicy(policyModel.getId().getPolicyId())
                .setRetain(policyModel.getRetain())
                .setQos(policyModel.getQos())
                .setSlightDelay(policyModel.getSlightDelay())
                .setMaxBigDelayTryCount(policyModel.getMaxBigDelayTryCount())
                .setMaxSlightDelayTryCount(policyModel.getMaxSlightDelayTryCount())
                .build());
    }
}
