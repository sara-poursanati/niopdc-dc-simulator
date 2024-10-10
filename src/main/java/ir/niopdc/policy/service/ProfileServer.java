package ir.niopdc.policy.service;

import io.grpc.stub.StreamObserver;
import ir.niopdc.common.entity.ProfileMessageDto;
import ir.niopdc.common.entity.ProfileTopicPolicyDto;
import ir.niopdc.common.grpc.config.*;
import ir.niopdc.common.grpc.profile.MGConfigServiceGrpc;
import ir.niopdc.common.grpc.profile.ProfileRequest;
import ir.niopdc.common.grpc.profile.ProfileResponse;
import ir.niopdc.common.grpc.profile.ProfileTopicPolicy;
import ir.niopdc.policy.facade.PolicyFacade;
import ir.niopdc.policy.facade.ProfileFacade;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@Slf4j
public class ProfileServer extends MGConfigServiceGrpc.MGConfigServiceImplBase {

    private ProfileFacade profileFacade;
    private PolicyFacade policyFacade;

    @Autowired
    public void setProfileFacade(ProfileFacade profileFacade) {
        this.profileFacade = profileFacade;
    }

    @Autowired
    public void setPolicyFacade(PolicyFacade policyFacade) {this.policyFacade = policyFacade;}

    @Override
    public void profile(ProfileRequest request, StreamObserver<ProfileResponse> responseObserver) {
        log.debug("A profile request received for gateway [{}]", request.getMediaGatewayId());
        ProfileMessageDto profileMessageModel = profileFacade.getProfile(request.getMediaGatewayId());
        sendProfileResponse(profileMessageModel, responseObserver);
    }

    private void sendProfileResponse(ProfileMessageDto profileMessageModel, StreamObserver<ProfileResponse> responseObserver) {
        getProfileTopicPolicies(profileMessageModel);


        ProfileResponse.Builder builder = buildProfileResponse(profileMessageModel);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private ProfileResponse.Builder buildProfileResponse(ProfileMessageDto profileMessageModel) {
        ProfileResponse.Builder builder =  ProfileResponse.newBuilder();
        builder.setAddress(profileMessageModel.getAddress())
                .setAreaId(profileMessageModel.getAreaId())
                .setName(profileMessageModel.getName())
                .setMediaGatewayId(profileMessageModel.getTerminalId())
                .setFuelStationId(profileMessageModel.getGsId())
                .setTerminalCount(profileMessageModel.getPtCount())
                .setZoneId(profileMessageModel.getZoneId())
                .addAllTopicPolicies(getProfileTopicPolicies(profileMessageModel));
        return builder;
    }

    private List<ProfileTopicPolicy> getProfileTopicPolicies(ProfileMessageDto profileMessageModel) {
        List<ProfileTopicPolicy> profileTopicPolicies = new ArrayList<>();
        for (ProfileTopicPolicyDto policyModel : profileMessageModel.getTopicPolicies()) {
            profileTopicPolicies.add(ProfileTopicPolicy
                    .newBuilder()
                    .setBigDelay(policyModel.getBigDelay())
                    .setPolicy(policyModel.getPolicy())
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
