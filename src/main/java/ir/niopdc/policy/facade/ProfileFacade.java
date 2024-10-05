package ir.niopdc.policy.facade;

import ir.niopdc.common.entity.ProfileMessageDto;
import ir.niopdc.common.entity.ProfileTopicPolicyDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ProfileFacade {

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

    private static ProfileMessageDto getProfileMessageModel(String gatewayId) {
        ProfileMessageDto profile = new ProfileMessageDto();
        profile.setTerminalId(gatewayId);
        profile.setAddress("تهران");
        profile.setName("تهرانپارس");
        profile.setZoneId("2552");
        profile.setAreaId("12");
        profile.setGsId("258");
        profile.setPtCount(11);
        return profile;
    }
}
