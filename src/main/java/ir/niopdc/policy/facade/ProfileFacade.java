package ir.niopdc.policy.facade;

import ir.niopdc.common.entity.ProfileMessageModel;
import ir.niopdc.common.entity.ProfileTopicPolicyModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ProfileFacade {

    private final Random random = new Random();

    public ProfileMessageModel getProfile(String gatewayId) {
        ProfileMessageModel profile = getProfileMessageModel(gatewayId);

        addPolicies(profile);

        return profile;
    }

    private void addPolicies(ProfileMessageModel profile) {
        List<ProfileTopicPolicyModel> policyList = new ArrayList<>();

        ProfileTopicPolicyModel policy1 = getProfileTopicPolicyModel1();
        policyList.add(policy1);
        ProfileTopicPolicyModel policy2 = getProfileTopicPolicyModel2();
        policyList.add(policy2);

        profile.setTopicPolicies(policyList);
    }

    private ProfileTopicPolicyModel getProfileTopicPolicyModel1() {
        ProfileTopicPolicyModel policy = new ProfileTopicPolicyModel();
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

    private ProfileTopicPolicyModel getProfileTopicPolicyModel2() {
        ProfileTopicPolicyModel policy = new ProfileTopicPolicyModel();
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

    private static ProfileMessageModel getProfileMessageModel(String gatewayId) {
        ProfileMessageModel profile = new ProfileMessageModel();
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
