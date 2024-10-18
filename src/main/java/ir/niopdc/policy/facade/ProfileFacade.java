package ir.niopdc.policy.facade;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import ir.niopdc.common.entity.ProfileMessageDto;
import ir.niopdc.common.entity.ProfileTopicPolicyDto;
import ir.niopdc.policy.domain.fuelstation.FuelStation;
import ir.niopdc.policy.domain.fuelstation.FuelStationService;
import ir.niopdc.policy.domain.fuelterminal.FuelTerminal;
import ir.niopdc.policy.domain.mediagateway.MediaGateway;
import ir.niopdc.policy.domain.mediagateway.MediaGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ProfileFacade {

    @Autowired
    private MediaGatewayService mediaGatewayService;
    @Autowired
    private FuelStationService fuelStationService;
    private final Random random = new Random();

    public ProfileMessageDto getProfile(String gatewayId) {
        ProfileMessageDto profile = getProfileMessageModel(gatewayId);

        addPolicies(profile);

        return profile;
    }

    private void addPolicies(ProfileMessageDto profile) {
        List<ProfileTopicPolicyDto> policyList = new ArrayList<>();

        ProfileTopicPolicyDto policy1 = getProfileTopicPolicyModel1();
        policyList.add(policy1);
        ProfileTopicPolicyDto policy2 = getProfileTopicPolicyModel2();
        policyList.add(policy2);

        profile.setTopicPolicies(policyList);
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

    private ProfileMessageDto getProfileMessageModel(String gatewayId) {
        MediaGateway mediaGateway = mediaGatewayService.findById(gatewayId);
        if (mediaGateway == null) {
            throw new StatusRuntimeException(Status.NOT_FOUND.withDescription("The media gateway not found."));
        }
        return getProfileMessageDto(mediaGateway);
    }

    private ProfileMessageDto getProfileMessageDto(MediaGateway mediaGateway) {
        FuelStation theFuelStation = fuelStationService.getFuelStationByMediaGateway(mediaGateway);
        ProfileMessageDto theProfile = new ProfileMessageDto();
        theProfile.setTerminalId(mediaGateway.getSerialNumber());
        theProfile.setAddress(theFuelStation.getAddress());
        theProfile.setName(theFuelStation.getName());
        theProfile.setZoneId(theFuelStation.getZoneId());
        theProfile.setAreaId(theFuelStation.getAreaId());
        theProfile.setGsId(theFuelStation.getId());
        theProfile.setPtCount(theFuelStation.getFuelTerminals().size());
        return theProfile;
    }
}
