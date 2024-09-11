package ir.nifss.policy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import ir.nifss.grpc.PolicyRequest;
import ir.nifss.grpc.PolicyResponse;
import ir.nifss.grpc.PolicyServiceGrpc;
import ir.nifss.policy.dto.PolicyDto;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@GrpcService
public class PolicyService extends PolicyServiceGrpc.PolicyServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(PolicyService.class);

    private PolicyFacade policyFacade;
    private ObjectMapper objectMapper;

    @Autowired
    public void setPolicyFacade(PolicyFacade policyFacade) {
        this.policyFacade = policyFacade;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void getFuelTypePolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for fuel type policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getFuelTypePolicy();
        sendResponse(policy, responseObserver);
    }

    @Override
    public void getQuotaPolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for fuel type policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getQuotaPolicy();
        sendResponse(policy, responseObserver);
    }

    @Override
    public void getBlackListPolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for fuel type policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getBlackListPolicy();
        sendResponse(policy, responseObserver);
    }

    @Override
    public void getWhiteListPolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for fuel type policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getWhiteListPolicy();
        sendResponse(policy, responseObserver);
    }

    @Override
    public void getGrayListPolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for fuel type policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getGrayListPolicy();
        sendResponse(policy, responseObserver);
    }

    @Override
    public void getCodingPolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for fuel type policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getCodingPolicy();
        sendResponse(policy, responseObserver);
    }

    @Override
    public void getTerminalSoftware(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        logger.info("A request for fuel type policy received for gsId: {}", request.getGsId());

        PolicyDto policy = policyFacade.getTerminalSoftware();
        sendResponse(policy, responseObserver);
    }

    private void sendResponse(PolicyDto policy, StreamObserver<PolicyResponse> responseObserver) {
        Struct.Builder dataBuilder = getStructBuilder(policy);

        PolicyResponse response = PolicyResponse.newBuilder()
                .setPolicyId(policy.getPolicyId())
                .setVersion(policy.getVersion())
                .setVersionName(policy.getVersionName())
                .setData(dataBuilder.build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private Struct.Builder getStructBuilder(PolicyDto policy) {
        Struct.Builder dataBuilder = Struct.newBuilder();
        try {
            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("data", policy.getData());
            String json = objectMapper.writeValueAsString(wrapper);
            logger.info(json);
            JsonFormat.parser().merge(json, dataBuilder);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return dataBuilder;
    }
}
