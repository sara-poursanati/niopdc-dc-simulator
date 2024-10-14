package ir.niopdc.policy.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import ir.niopdc.common.entity.ProfileMessageDto;
import ir.niopdc.common.entity.ProfileTopicPolicyDto;
import ir.niopdc.common.grpc.profile.MGConfigServiceGrpc;
import ir.niopdc.common.grpc.profile.ProfileRequest;
import ir.niopdc.common.grpc.profile.ProfileResponse;
import ir.niopdc.common.grpc.profile.ProfileTopicPolicy;
import ir.niopdc.policy.dto.DataDto;
import ir.niopdc.policy.facade.PolicyFacade;
import ir.niopdc.policy.facade.ProfileFacade;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;

@GrpcService
@Slf4j
public class ProfileServer extends MGConfigServiceGrpc.MGConfigServiceImplBase {

    private ProfileFacade profileFacade;
    private PolicyFacade policyFacade;

    @Value("${app.chunkSize}")
    private int chunkSize;

    @Value("${app.blackList.path}")
    private String blackListPath;

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

//    private void sendDataResponse(DataDto data, StreamObserver<CommonConfigResponse> responseObserver) {
//        Path filePath = Path.of(blackListPath);
//        log.info("Sending file started at {}", LocalDateTime.now());
//        try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {
//            Spliterator<String> split = stream.spliterator();
//
//            while(true) {
//                List<String> chunk = getChunk(split);
//                if (chunk.isEmpty()) {
//                    break;
//                }
//                String item = String.join("", chunk);
//                responseObserver.onNext(CommonConfigResponse.newBuilder().setContent(ByteString.copyFromUtf8(item)).build());
//            }
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//        }
//
//        responseObserver.onCompleted();
//        log.info("Sending file ended at {}", LocalDateTime.now());
//    }
//
//    private List<String> getChunk(Spliterator<String> split) {
//        List<String> chunk = new ArrayList<>(chunkSize);
//        for (int index = 0; index < chunkSize; index++) {
//            if (!split.tryAdvance(chunk::add)) {
//                break;
//            }
//        }
//        return chunk;
//    }

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
