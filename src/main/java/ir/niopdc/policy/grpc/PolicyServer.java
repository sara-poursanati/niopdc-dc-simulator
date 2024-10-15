package ir.niopdc.policy.grpc;

import io.grpc.stub.StreamObserver;
import ir.niopdc.common.grpc.policy.MGPolicyServiceGrpc;
import ir.niopdc.common.grpc.policy.PolicyRequest;
import ir.niopdc.common.grpc.policy.RateResponse;
import ir.niopdc.policy.facade.PolicyFacade;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
@Slf4j
public class PolicyServer extends MGPolicyServiceGrpc.MGPolicyServiceImplBase {

    private PolicyFacade policyFacade;

    @Autowired
    public void setPolicyFacade(PolicyFacade policyFacade) {this.policyFacade = policyFacade;}

    @Override
    public void rate(PolicyRequest request, StreamObserver<RateResponse> responseObserver) {
        try {
            RateResponse response = policyFacade.getFuelRatePolicy();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception exp) {
            log.error(exp.getMessage(), exp);
            responseObserver.onError(exp);
        }
    }

//    @Override
//    public void nationalQuota(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) {
//        try {
//            PolicyDto policy = policyFacade.getNationalQuotaPolicy();
//            sendResponse(request, responseObserver, policy);
//        } catch (Exception exp) {
//            log.error(exp.getMessage(), exp);
//            responseObserver.onError(exp);
//        } finally {
//            responseObserver.onCompleted();
//        }
//    }
//
//    @Override
//    public void regionalQuota(PolicyRequest request, StreamObserver<RegionalQuotaResponse> responseObserver) {
//        try {
//            PolicyDto policy = policyFacade.getRegionalQuotaPolicy();
//            sendResponse(request, responseObserver, policy);
//        } catch (Exception exp) {
//            log.error(exp.getMessage(), exp);
//            responseObserver.onError(exp);
//        } finally {
//            responseObserver.onCompleted();
//        }
//    }
//
//    private void sendResponse(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver, PolicyDto policy) throws JsonProcessingException {
//        String jsonPackage = objectWriter.writeValueAsString(policy);
//        log.info(jsonPackage);
//        PolicyResponse policyResponse = PolicyResponse
//                .newBuilder()
//                .setFuelStationId(request.getFuelStationId())
//                .setContent(ByteString.copyFromUtf8(jsonPackage))
//                .build();
//        responseObserver.onNext(policyResponse);
//    }

}
