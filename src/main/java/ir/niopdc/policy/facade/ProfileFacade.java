package ir.niopdc.policy.facade;

import ir.niopdc.common.entity.ProfileMessageModel;
import ir.niopdc.common.entity.ProfileTopicPolicyModel;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ProfileFacade {

    public ProfileMessageModel getProfile(String gatewayId) {
        ProfileMessageModel profile = getProfileMessageModel(gatewayId);

        addPolicies(profile);

        return profile;
    }

    private static void addPolicies(ProfileMessageModel profile) {
        List<ProfileTopicPolicyModel> policyList = new ArrayList<>();

        Random random = new Random();
        for (int index =0; index < 5; index++) {
            ProfileTopicPolicyModel policy = getProfileTopicPolicyModel(random);
            policyList.add(policy);
        }
        profile.setTopicPolicies(policyList);
    }

    private static ProfileTopicPolicyModel getProfileTopicPolicyModel(Random random) {
        ProfileTopicPolicyModel policy = new ProfileTopicPolicyModel();
        policy.setPolicy(Integer.parseInt(RandomStringUtils.random(2, false, true)));
        policy.setBigDelay(Long.parseLong(RandomStringUtils.random(10, false, true)));
        policy.setQos(Byte.parseByte(RandomStringUtils.random(1, false, true)));
        policy.setRetain(random.nextBoolean());
        policy.setSlightDelay(Long.parseLong(RandomStringUtils.random(10, false, true)));
        policy.setMaxBigDelayTryCount(Integer.parseInt(RandomStringUtils.random(5, false, true)));
        policy.setMaxSlightDelayTryCount(Integer.parseInt(RandomStringUtils.random(5, false, true)));
        policy.setPublishTopicTitle(RandomStringUtils.random(20, false, true));
        policy.setSubscribeTopicTitle(RandomStringUtils.random(20, false, true));
        return policy;
    }

    private static ProfileMessageModel getProfileMessageModel(String gatewayId) {
        ProfileMessageModel profile = new ProfileMessageModel();
        profile.setTerminalId(gatewayId);
        profile.setAddress(RandomStringUtils.random(100, true, true));
        profile.setName(RandomStringUtils.random(50, true, true));
        profile.setZoneId(RandomStringUtils.random(2, false, true));
        profile.setAreaId(RandomStringUtils.random(3, false, true));
        profile.setGsId(RandomStringUtils.random(4, false, true));
        profile.setPtCount(Integer.parseInt(RandomStringUtils.random(2, false, true)));
        return profile;
    }
}
