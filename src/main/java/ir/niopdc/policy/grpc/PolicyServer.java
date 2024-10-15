package ir.niopdc.policy.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import ir.niopdc.common.grpc.policy.FilePolicyResponse;
import ir.niopdc.common.grpc.policy.MGPolicyServiceGrpc;
import ir.niopdc.common.grpc.policy.PolicyRequest;
import ir.niopdc.common.grpc.policy.RateResponse;
import ir.niopdc.policy.dto.FilePolicyResponseDto;
import ir.niopdc.policy.facade.PolicyFacade;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@GrpcService
@Slf4j
public class PolicyServer extends MGPolicyServiceGrpc.MGPolicyServiceImplBase {

    private PolicyFacade policyFacade;

    @Autowired
    public void setPolicyFacade(PolicyFacade policyFacade) {
        this.policyFacade = policyFacade;
    }

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

    @Override
    public void nationalQuota(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) {
        try {
            FilePolicyResponseDto dto = policyFacade.getNationalQuotaPolicy();
            log.info("Sending file started at {}", LocalDateTime.now());
            byte[] bytes = Files.readAllBytes(dto.getFile());

            FilePolicyResponse.Builder builder = FilePolicyResponse.newBuilder()
                    .setMetadata(dto.getMetadata())
                    .setFile(ByteString.copyFrom(bytes));
            responseObserver.onNext(builder.build());

            responseObserver.onCompleted();
            log.info("Sending file ended at {}", LocalDateTime.now());
        } catch (Exception exp) {
            log.error(exp.getMessage(), exp);
            responseObserver.onError(exp);
        }
    }

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
