package ir.niopdc.policy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import ir.niopdc.common.entity.policy.PolicyDto;
import ir.niopdc.common.grpc.policy.MGPolicyServiceGrpc;
import ir.niopdc.common.grpc.policy.PolicyRequest;
import ir.niopdc.common.grpc.policy.PolicyResponse;
import ir.niopdc.policy.facade.PolicyFacade;
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
public class PolicyServer extends MGPolicyServiceGrpc.MGPolicyServiceImplBase {

    private PolicyFacade policyFacade;
    private ObjectWriter objectWriter;

    @Value("${app.chunkSize}")
    private int chunkSize;

    @Value("${app.blackList.path}")
    private String blackListPath;

    @Autowired
    public void setPolicyFacade(PolicyFacade policyFacade) {
        this.policyFacade = policyFacade;
    }

    @Autowired
    public void setObjectWriter(ObjectWriter objectWriter) {
        this.objectWriter = objectWriter;
    }

    @Override
    public void rate(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        try {
            PolicyDto response = policyFacade.getFuelRatePolicy();
            sendJsonPolicyResponse(response, responseObserver);
        } catch (JsonProcessingException exp) {
            throw new IllegalStateException(exp);
        }
    }

    @Override
    public void blackList(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        sendDataResponse(responseObserver);
    }

    private void sendJsonPolicyResponse(PolicyDto policy, StreamObserver<PolicyResponse> responseObserver) {
        try {
            String json = objectWriter.writeValueAsString(policy);
            responseObserver.onNext(
                    PolicyResponse.newBuilder().setContent(ByteString.copyFromUtf8(json)).build());
            responseObserver.onCompleted();
        } catch (JsonProcessingException e) {
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }

    }

    private void sendDataResponse(StreamObserver<PolicyResponse> responseObserver) {
        Path filePath = Path.of(blackListPath);
        log.info("Sending file started at {}", LocalDateTime.now());
        try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {
            Spliterator<String> split = stream.spliterator();

            while(true) {
                List<String> chunk = getChunk(split);
                if (chunk.isEmpty()) {
                    break;
                }
                String item = String.join("", chunk);
                responseObserver.onNext(PolicyResponse.newBuilder().setContent(ByteString.copyFromUtf8(item)).build());
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        responseObserver.onCompleted();
        log.info("Sending file ended at {}", LocalDateTime.now());
    }

    private List<String> getChunk(Spliterator<String> split) {
        List<String> chunk = new ArrayList<>(chunkSize);
        for (int index = 0; index < chunkSize; index++) {
            if (!split.tryAdvance(chunk::add)) {
                break;
            }
        }
        return chunk;
    }
}
