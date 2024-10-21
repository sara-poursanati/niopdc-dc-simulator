package ir.niopdc.policy.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import ir.niopdc.common.grpc.policy.*;
import ir.niopdc.policy.dto.FilePolicyResponseDto;
import ir.niopdc.policy.facade.PolicyFacade;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
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

    @Value("${app.chunkSize}")
    private int chunkSize;

    private PolicyFacade policyFacade;

    @Autowired
    public void setPolicyFacade(PolicyFacade policyFacade) {
        this.policyFacade = policyFacade;
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
        FilePolicyResponseDto dto = policyFacade.getBlackListPolicy();
        sendCsvFile(responseObserver, dto);
    }

    @Override
    @SneakyThrows
    public void coding(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) {
        FilePolicyResponseDto dto = policyFacade.getCodingPolicy();
        sendCsvFile(responseObserver, dto);
    }

    @Override
    @SneakyThrows
    public void grayList(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) {
        FilePolicyResponseDto dto = policyFacade.getGrayListPolicy();
        sendCsvFile(responseObserver, dto);
    }

    private void sendCsvFile(StreamObserver<FilePolicyResponse> responseObserver, FilePolicyResponseDto dto) throws IOException {
        log.info("Sending file started at {}", LocalDateTime.now());
        try (Stream<String> stream = Files.lines(dto.getFile(), StandardCharsets.UTF_8)) {
            Spliterator<String> split = stream.spliterator();

            while (true) {
                List<String> chunk = getChunk(split);
                if (chunk.isEmpty()) {
                    break;
                }
                String item = String.join("", chunk);
                responseObserver.onNext(FilePolicyResponse.newBuilder()
                        .setFile(ByteString.copyFromUtf8(item))
                        .setMetadata(dto.getMetadata()).build());
                responseObserver.onNext(FilePolicyResponse.newBuilder().setFile(ByteString.copyFromUtf8(item)).build());
            }
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

}
