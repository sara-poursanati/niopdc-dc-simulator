package ir.niopdc.policy.facade;

import ir.niopdc.common.entity.ProfileTopicPolicyDto;
import ir.niopdc.common.grpc.profile.ProfileResponse;
import ir.niopdc.common.grpc.profile.ProfileTopicPolicy;
import ir.niopdc.policy.domain.fuelstation.FuelStation;
import ir.niopdc.policy.domain.fuelstationpolicy.FuelStationPolicy;
import ir.niopdc.policy.domain.fuelstationpolicy.FuelStationPolicyService;
import ir.niopdc.policy.domain.fuelterminal.FuelTerminalService;
import ir.niopdc.policy.domain.mediagateway.MediaGateway;
import ir.niopdc.policy.domain.mediagateway.MediaGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ProfileFacade {

    private MediaGatewayService mediaGatewayService;
    private FuelStationPolicyService fuelStationPolicyService;
    private FuelTerminalService fuelTerminalService;

    @Autowired
    private void setMediaGatewayService(MediaGatewayService theMediaGatewayService) {
        this.mediaGatewayService = theMediaGatewayService;
    }

    @Autowired
    private void setFuelStationPolicyService(FuelStationPolicyService theFuelStationPolicyService) {
        this.fuelStationPolicyService = theFuelStationPolicyService;
    }

    @Autowired
    private void setFuelTerminalService(FuelTerminalService theFuelTerminalService) {
        this.fuelTerminalService = theFuelTerminalService;
    }

    private final Random random = new Random();

    public ProfileResponse.Builder getProfile(String gatewayId) {
        var unbuildedResponse = getProfileMessageModel(gatewayId);

        return unbuildedResponse;
    }

    private ProfileTopicPolicyDto getProfileTopicPolicyModel1() {
        ProfileTopicPolicyDto policy = new ProfileTopicPolicyDto();
        policy.setPolicy(1);
        policy.setBigDelay(10L);
        policy.setQos(Byte.valueOf("2"));
        policy.setRetain(false);
        policy.setSlightDelay(13L);
        policy.setMaxBigDelayTryCount(2);
        policy.setMaxSlightDelayTryCount(4);
        policy.setPublishTopicTitle("mg/pub/258/0/1");
        policy.setSubscribeTopicTitle("qms/pub/258/0/1");
        return policy;
    }

    private ProfileTopicPolicyDto getProfileTopicPolicyModel2() {
        ProfileTopicPolicyDto policy = new ProfileTopicPolicyDto();
        policy.setPolicy(4);
        policy.setBigDelay(20L);
        policy.setQos(Byte.valueOf("2"));
        policy.setRetain(false);
        policy.setSlightDelay(11L);
        policy.setMaxBigDelayTryCount(2);
        policy.setMaxSlightDelayTryCount(3);
        policy.setPublishTopicTitle("mg/pub/258/0/4");
        policy.setSubscribeTopicTitle("qms/pub/258/0/4");
        return policy;
    }

    private ProfileResponse.Builder getProfileMessageModel(String gatewayId) {
        MediaGateway mediaGateway = mediaGatewayService.findById(gatewayId);
        if (mediaGateway == null) {
            throw new IllegalArgumentException();
        }
        return buildProfileResponse(mediaGateway);
    }

    private ProfileResponse.Builder buildProfileResponse(MediaGateway mediaGateway) {
        FuelStation theFuelStation = mediaGateway.getFuelStation();
        List<FuelStationPolicy> fuelStationPolicis = fuelStationPolicyService.findFuelStationPolicyByFuelStationId(theFuelStation.getId());
        ProfileResponse.Builder builder =  ProfileResponse.newBuilder();
        builder.setAddress(theFuelStation.getAddress())
                .setAreaId(theFuelStation.getAreaId())
                .setName(theFuelStation.getName())
                .setMediaGatewayId(mediaGateway.getSerialNumber())
                .setFuelStationId(theFuelStation.getId())
                .setTerminalCount(fuelTerminalService.getPtCountByFuelStation(theFuelStation))
                .setZoneId(theFuelStation.getZoneId())
                .addAllTopicPolicies(getProfileTopicPolicies(fuelStationPolicis));
        return builder;
    }

    private List<ProfileTopicPolicy> getProfileTopicPolicies(List<FuelStationPolicy> fuelStationPolicies) {
        List<ProfileTopicPolicy> profileTopicPolicies = new ArrayList<>();
        for (FuelStationPolicy policyModel : fuelStationPolicies) {
            profileTopicPolicies.add(ProfileTopicPolicy
                    .newBuilder()
                    .setBigDelay(policyModel.getBigDelay())
                    .setPolicy(policyModel.getId().getPolicyId())
                    .setRetain(policyModel.getRetain())
                    .setQos(policyModel.getQos())
                    .setSlightDelay(policyModel.getSlightDelay())
                    .setPublishTopicTitle(policyModel.getPublishTopicTitle())
                    .setMaxBigDelayTryCount(policyModel.getMaxBigDelayTryCount())
                    .setSubscribeTopicTitle(policyModel.getSubscribeTopicTitle())
                    .setMaxSlightDelayTryCount(policyModel.getMaxSlightDelayTryCount())
                    .build());
        }
        return profileTopicPolicies;
    }
}
