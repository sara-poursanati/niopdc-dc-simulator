package ir.niopdc.policy.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import ir.niopdc.common.grpc.policy.PolicyRequest;
import ir.niopdc.common.grpc.policy.PolicyResponse;
import ir.niopdc.common.grpc.policy.PolicyServiceGrpc;
import ir.niopdc.policy.dto.PolicyDto;
import ir.niopdc.policy.facade.PolicyFacade;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class PolicyServer extends PolicyServiceGrpc.PolicyServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(PolicyServer.class);

    private PolicyFacade policyFacade;

    @Autowired
    public void setPolicyFacade(PolicyFacade policyFacade) {
        this.policyFacade = policyFacade;
    }

    @Override
    public void getFuelTypePolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for fuel type policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getFuelTypePolicy();

        sendResponse(policy, responseObserver);
    }

    @Override
    public void getQuotaPolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for quota policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getQuotaPolicy();
        sendResponse(policy, responseObserver);
    }

    @Override
    public void getBlackListPolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for black list policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getBlackListPolicy();
        sendResponse(policy, responseObserver);
    }

    @Override
    public void getLocalQuotaPolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for local quota policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getLocalQuotaPolicy();
        sendResponse(policy, responseObserver);
    }

    @Override
    public void getGrayListPolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for gray list policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getGrayListPolicy();
        sendResponse(policy, responseObserver);
    }

    @Override
    public void getCodingPolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for coding policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getCodingPolicy();
        sendResponse(policy, responseObserver);
    }

    @Override
    public void getTerminalSoftware(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for terminal software received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getTerminalSoftware();
        sendResponse(policy, responseObserver);
    }

    private void sendResponse(PolicyDto policy, StreamObserver<PolicyResponse> responseObserver) {
        for (String value : policy.getCsvList()) {
            responseObserver.onNext(
                    PolicyResponse.newBuilder().setData(ByteString.copyFromUtf8(value)).build());
        }
        responseObserver.onCompleted();
    }

}
