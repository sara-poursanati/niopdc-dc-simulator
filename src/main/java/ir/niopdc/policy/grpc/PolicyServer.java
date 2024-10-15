package ir.niopdc.policy.grpc;

import com.google.protobuf.ByteString;
import io.grpc.netty.shaded.io.netty.buffer.ByteBuf;
import io.grpc.stub.StreamObserver;
import ir.niopdc.common.grpc.policy.*;
import ir.niopdc.policy.dto.FilePolicyResponseDto;
import ir.niopdc.policy.facade.PolicyFacade;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
            sendBinaryFile(responseObserver, dto);
        } catch (Exception exp) {
            log.error(exp.getMessage(), exp);
            responseObserver.onError(exp);
        }
    }

    @Override
    public void terminalApp(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) {
        try {
            FilePolicyResponseDto dto = policyFacade.getTerminalSoftware();
            sendBinaryFile(responseObserver, dto);
        } catch (Exception exp) {
            log.error(exp.getMessage(), exp);
            responseObserver.onError(exp);
        }
    }

    @Override
    public void regionalQuota(PolicyRequest request, StreamObserver<RegionalQuotaResponse> responseObserver) {
        try {
            RegionalQuotaResponse response = policyFacade.getRegionalQuotaPolicy();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception exp) {
            log.error(exp.getMessage(), exp);
            responseObserver.onError(exp);
        }
    }

    @Override
    public void blackList(PolicyRequest request, StreamObserver<BlackListResponse> responseObserver) {
        try {
            FilePolicyResponseDto dto = policyFacade.getBlackListPolicy();
            sendBlackList(responseObserver, dto);
        } catch (Exception exp) {
            log.error(exp.getMessage(), exp);
            responseObserver.onError(exp);
        }
    }

    private void sendBlackList(StreamObserver<BlackListResponse> responseObserver, FilePolicyResponseDto dto) throws IOException {
        log.info("Sending file started at {}", LocalDateTime.now());

        responseObserver.onNext(BlackListResponse.newBuilder().setMetadata(dto.getMetadata()).build());
        try (Stream<String> stream = Files.lines(dto.getFile(), StandardCharsets.UTF_8)) {
            stream.forEach(item -> {
                BlackListMessage message = BlackListMessage.newBuilder()
                        .setCardId(item)
                        .setOperation(operationEnum.INSERT)
                        .build();
                responseObserver.onNext(BlackListResponse.newBuilder().addBlackListMessage(message).build());
            });
        }
        log.info("Sending file ended at {}", LocalDateTime.now());
    }

    private static void sendBinaryFile(StreamObserver<FilePolicyResponse> responseObserver, FilePolicyResponseDto dto) throws IOException {
        log.info("Sending file started at {}", LocalDateTime.now());
        byte[] bytes = Files.readAllBytes(dto.getFile());

        FilePolicyResponse.Builder builder = FilePolicyResponse.newBuilder()
                .setMetadata(dto.getMetadata())
                .setFile(ByteString.copyFrom(bytes));
        responseObserver.onNext(builder.build());

        responseObserver.onCompleted();
        log.info("Sending file ended at {}", LocalDateTime.now());
    }

}
