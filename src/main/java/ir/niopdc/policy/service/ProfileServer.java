package ir.niopdc.policy.service;

import io.grpc.stub.StreamObserver;
import ir.niopdc.common.entity.ProfileMessageModel;
import ir.niopdc.common.entity.ProfileTopicPolicyModel;
import ir.niopdc.common.grpc.profile.ProfileRequest;
import ir.niopdc.common.grpc.profile.ProfileResponse;
import ir.niopdc.common.grpc.profile.ProfileServiceGrpc;
import ir.niopdc.common.grpc.profile.ProfileTopicPolicy;
import ir.niopdc.policy.facade.ProfileFacade;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class ProfileServer extends ProfileServiceGrpc.ProfileServiceImplBase {

    private ProfileFacade profileFacade;

    @Autowired
    public void setProfileFacade(ProfileFacade profileFacade) {
        this.profileFacade = profileFacade;
    }

    @Override
    public void getProfile(ProfileRequest request, StreamObserver<ProfileResponse> responseObserver) {
        ProfileMessageModel profileMessageModel = profileFacade.getProfile(request.getTerminalId());
        sendResponse(profileMessageModel, responseObserver);
    }

    private void sendResponse(ProfileMessageModel profileMessageModel, StreamObserver<ProfileResponse> responseObserver) {
        getProfileTopicPolicies(profileMessageModel);


        ProfileResponse.Builder builder = buildResponse(profileMessageModel);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private ProfileResponse.Builder buildResponse(ProfileMessageModel profileMessageModel) {
        ProfileResponse.Builder builder =  ProfileResponse.newBuilder();
        builder.setAddress(profileMessageModel.getAddress())
                .setAreaId(profileMessageModel.getAreaId())
                .setName(profileMessageModel.getName())
                .setTerminalId(profileMessageModel.getTerminalId())
                .setGsId(profileMessageModel.getGsId())
                .setPtCount(profileMessageModel.getPtCount())
                .setZoneId(profileMessageModel.getZoneId())
                .addAllTopicPolicies(getProfileTopicPolicies(profileMessageModel));
        return builder;
    }

    private List<ProfileTopicPolicy> getProfileTopicPolicies(ProfileMessageModel profileMessageModel) {
        List<ProfileTopicPolicy> profileTopicPolicies = new ArrayList<>();
        for (ProfileTopicPolicyModel policyModel : profileMessageModel.getTopicPolicies()) {
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
