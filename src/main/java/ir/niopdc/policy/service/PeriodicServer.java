package ir.niopdc.policy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import ir.niopdc.common.grpc.config.CommonConfigResponse;
import ir.niopdc.common.grpc.online.CommonOnlineRequest;
import ir.niopdc.common.grpc.online.CommonOnlineResponse;
import ir.niopdc.common.grpc.online.MGOnlineServiceGrpc;
import ir.niopdc.policy.dto.PolicyMetadata;
import ir.niopdc.policy.dto.PolicyResponse;
import ir.niopdc.policy.facade.PolicyFacade;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
@Slf4j
public class PeriodicServer extends MGOnlineServiceGrpc.MGOnlineServiceImplBase {

    private PolicyFacade policyFacade;

    @Autowired
    public void setPolicyFacade(PolicyFacade policyFacade) {
        this.policyFacade = policyFacade;
    }

    @Override
    public void rate(CommonOnlineRequest request, StreamObserver<CommonOnlineResponse> responseObserver) {
        try {
            PolicyResponse response = policyFacade.getFuelRatePolicy();
            sendJsonPolicyResponse(response, responseObserver);
        } catch (JsonProcessingException exp) {
            throw new IllegalStateException(exp);
        }
    }

    private void sendJsonPolicyResponse(PolicyResponse policy, StreamObserver<CommonOnlineResponse> responseObserver) {
        responseObserver.onNext(
                CommonOnlineResponse.newBuilder().setFile(ByteString.copyFromUtf8(policy.getJsonContent())).build());
        responseObserver.onCompleted();
    }

}
