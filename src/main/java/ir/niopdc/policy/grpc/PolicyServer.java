package ir.niopdc.policy.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import ir.niopdc.common.entity.policy.BlackListDto;
import ir.niopdc.common.grpc.policy.*;
import ir.niopdc.policy.config.AppConfig;
import ir.niopdc.policy.dto.FilePolicyResponseDto;
import ir.niopdc.policy.dto.ListResponseDto;
import ir.niopdc.policy.facade.PolicyFacade;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;

@GrpcService
@Slf4j
public class PolicyServer extends MGPolicyServiceGrpc.MGPolicyServiceImplBase {

    private AppConfig appConfig;

    private PolicyFacade policyFacade;

    @Autowired
    public void setPolicyFacade(PolicyFacade policyFacade) {
        this.policyFacade = policyFacade;
    }

    @Autowired
    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void rate(PolicyRequest request, StreamObserver<RateResponse> responseObserver) {
        RateResponse response = policyFacade.getFuelRatePolicy(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @SneakyThrows
    public void nationalQuota(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) {
        FilePolicyResponseDto dto = policyFacade.getNationalQuotaPolicy(request);
        sendBinaryFile(responseObserver, dto);
    }


    @Override
    @SneakyThrows
    public void terminalApp(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) {
        FilePolicyResponseDto dto = policyFacade.getTerminalSoftware(request);
        sendBinaryFile(responseObserver, dto);
    }

    @Override
    public void regionalQuota(PolicyRequest request, StreamObserver<RegionalQuotaResponse> responseObserver) {
        RegionalQuotaResponse response = policyFacade.getRegionalQuotaPolicy(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @SneakyThrows
    public void blackList(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) {
        //if (request.getMode() == ModeEnumMessage.CONFIG) {
            sendCompleteBlackList(responseObserver);
       // } else {
       //     sendDifferentialBlackList(request, responseObserver);
       // }
    }

    @Override
    @SneakyThrows
    public void coding(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) {
        if (request.getMode() == ModeEnumMessage.CONFIG) {
            sendCompleteCodingList(responseObserver);
        } else {
            sendDifferentialCodingList(request, responseObserver);
        }
    }

    @Override
    @SneakyThrows
    public void grayList(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) {
        if (request.getMode() == ModeEnumMessage.CONFIG) {
            sendCompleteGrayList(responseObserver);
        } else {
            sendDifferentialGrayList(request, responseObserver);
        }
    }

    private void sendCompleteGrayList(StreamObserver<FilePolicyResponse> responseObserver) throws IOException {
        FilePolicyResponseDto dto = policyFacade.getCompleteGrayList();
        sendChunkedBinaryFile(responseObserver, dto);
    }

    private void sendCompleteBlackList(StreamObserver<FilePolicyResponse> responseObserver) throws IOException {
        FilePolicyResponseDto dto = policyFacade.getCompleteBlackList();
        sendChunkedBinaryFile(responseObserver, dto);
    }

    private void sendCompleteCodingList(StreamObserver<FilePolicyResponse> responseObserver) throws IOException {
        FilePolicyResponseDto dto = policyFacade.getCompleteCodingList();
        sendChunkedBinaryFile(responseObserver, dto);
    }

    private void sendDifferentialBlackList(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) throws IOException {
        ListResponseDto listResponseDto = policyFacade.getDifferentialBlackList(request);
        sendDifferentialList(listResponseDto, responseObserver);
    }

    private void sendDifferentialCodingList(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) throws IOException {
        ListResponseDto listResponseDto = policyFacade.getDifferentialCodingList(request);
        sendDifferentialList(listResponseDto, responseObserver);
    }

    private void sendDifferentialGrayList(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) throws IOException {
        ListResponseDto listResponseDto = policyFacade.getDifferentialGrayList(request);
        sendDifferentialList(listResponseDto, responseObserver);
    }

    private static void sendBinaryFile(StreamObserver<FilePolicyResponse> responseObserver, FilePolicyResponseDto dto) throws IOException {
        log.info("Sending file started at {}", LocalDateTime.now());

        FilePolicyResponse.Builder builder = FilePolicyResponse.newBuilder()
                .setMetadata(dto.getMetadata());
        if (dto.getFile() != null) {
            byte[] bytes = Files.readAllBytes(dto.getFile());
            builder.setFile(ByteString.copyFrom(bytes));
        }

        responseObserver.onNext(builder.build());

        responseObserver.onCompleted();
        log.info("Sending file ended at {}", LocalDateTime.now());
    }

    private void sendChunkedBinaryFile(StreamObserver<FilePolicyResponse> responseObserver, FilePolicyResponseDto dto) throws IOException {
        log.info("Sending file {} started at {}", dto.getFile().toString(), LocalDateTime.now());
        responseObserver.onNext(FilePolicyResponse.newBuilder()
                .setMetadata(dto.getMetadata()).build());
        try (InputStream input = new FileInputStream(dto.getFile().toFile())) {
            byte[] bytes = new byte[IOUtils.DEFAULT_BUFFER_SIZE];
            while (IOUtils.read(input, bytes) > 0) {
                responseObserver.onNext(FilePolicyResponse.newBuilder().setFile(ByteString.copyFrom(bytes)).build());
            }
        }
        responseObserver.onCompleted();
        log.info("Sending file ended at {}", LocalDateTime.now());
    }

    private void sendDifferentialList(ListResponseDto listResponseDto, StreamObserver<FilePolicyResponse> responseObserver) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);) {
            objectOutputStream.writeObject(listResponseDto.getObjects());
            byte[] fileBytes = outputStream.toByteArray();
            responseObserver.onNext(FilePolicyResponse
                    .newBuilder()
                    .setMetadata(listResponseDto.getMetadata())
                    .setFile(ByteString.copyFrom(fileBytes)).build());
            responseObserver.onCompleted();
        }
    }
}
