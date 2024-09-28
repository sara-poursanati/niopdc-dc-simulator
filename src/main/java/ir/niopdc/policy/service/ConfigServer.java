package ir.niopdc.policy.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import ir.niopdc.common.entity.ProfileMessageModel;
import ir.niopdc.common.entity.ProfileTopicPolicyModel;
import ir.niopdc.common.grpc.config.*;
import ir.niopdc.policy.dto.DataDto;
import ir.niopdc.policy.dto.PolicyDto;
import ir.niopdc.policy.facade.PolicyFacade;
import ir.niopdc.policy.facade.ProfileFacade;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@GrpcService
@Slf4j
public class ConfigServer extends MGConfigServiceGrpc.MGConfigServiceImplBase {

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
        log.debug("A profile request received for gateway [{}]", request.getTerminalId());
        ProfileMessageModel profileMessageModel = profileFacade.getProfile(request.getTerminalId());
        sendProfileResponse(profileMessageModel, responseObserver);
    }
    
    @Override
    public void rate(CommonConfigRequest request, StreamObserver<CommonConfigResponse> responseObserver) {
        log.debug("A rate request received for gs [{}]", request.getGsId());
        PolicyDto policy = policyFacade.getFuelTypePolicy();
        sendPolicyResponse(policy, responseObserver);
    }

    @Override
    @Transactional(readOnly = true)
    public void blackList(CommonConfigRequest request, StreamObserver<CommonConfigResponse> responseObserver) {
        DataDto dataDto = policyFacade.getBlackListPolicy();
        sendDataResponse(dataDto, responseObserver);
    }

    private void sendPolicyResponse(PolicyDto policy, StreamObserver<CommonConfigResponse> responseObserver) {
        for (String value : policy.getCsvList()) {
            responseObserver.onNext(
                    CommonConfigResponse.newBuilder().setChunkFile(ByteString.copyFromUtf8(value)).build());
        }
        responseObserver.onCompleted();
    }

    private void sendDataResponse(DataDto data, StreamObserver<CommonConfigResponse> responseObserver) {
        Path filePath = Path.of("D:\\work\\transmition\\mccsc\\black-list-sample.csv");
        log.info("Sending file started at {}", LocalDateTime.now());
        try (Stream<String> stream = Files.lines(Paths.get(filePath.toUri()), StandardCharsets.UTF_8)) {
            Spliterator<String> split = stream.spliterator();
            int chunkSize = 10000;

            while(true) {
                List<String> chunk = new ArrayList<>(chunkSize);
                for (int i = 0; i < chunkSize && split.tryAdvance(chunk::add); i++){
                }
                if (chunk.isEmpty()) break;
                String item = String.join("", chunk);
                responseObserver.onNext(CommonConfigResponse.newBuilder().setChunkFile(ByteString.copyFromUtf8(item)).build());
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        log.info("Sending file ended at {}", LocalDateTime.now());
        responseObserver.onCompleted();
    }

    private void sendProfileResponse(ProfileMessageModel profileMessageModel, StreamObserver<ProfileResponse> responseObserver) {
        getProfileTopicPolicies(profileMessageModel);


        ProfileResponse.Builder builder = buildProfileResponse(profileMessageModel);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private ProfileResponse.Builder buildProfileResponse(ProfileMessageModel profileMessageModel) {
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
