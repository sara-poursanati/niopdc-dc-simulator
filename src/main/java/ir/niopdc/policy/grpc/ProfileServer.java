package ir.niopdc.policy.grpc;

import io.grpc.stub.StreamObserver;
import ir.niopdc.common.grpc.profile.MGConfigServiceGrpc;
import ir.niopdc.common.grpc.profile.ProfileRequest;
import ir.niopdc.common.grpc.profile.ProfileResponse;
import ir.niopdc.policy.facade.ProfileFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class ProfileServer extends MGConfigServiceGrpc.MGConfigServiceImplBase {

    private static final Logger logger = LogManager.getLogger(ProfileServer.class);

    private ProfileFacade profileFacade;

    @Autowired
    public void setProfileFacade(ProfileFacade profileFacade) {
        this.profileFacade = profileFacade;
    }

    @Override
    public void profile(ProfileRequest request, StreamObserver<ProfileResponse> responseObserver) {
        logger.info("Received profile request: {}", request);  // Example log statement
        ProfileResponse response = profileFacade.getProfile(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        logger.info("Profile response sent: {}", response);  // Example log statement
    }
}
