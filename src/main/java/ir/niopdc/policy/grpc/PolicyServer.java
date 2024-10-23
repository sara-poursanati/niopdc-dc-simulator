package ir.niopdc.policy.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import ir.niopdc.common.entity.policy.BlackListDto;
import ir.niopdc.common.entity.policy.CodingDto;
import ir.niopdc.common.entity.policy.OperationEnum;
import ir.niopdc.common.grpc.policy.*;
import ir.niopdc.policy.config.AppConfig;
import ir.niopdc.policy.domain.coding.CodingList;
import ir.niopdc.policy.dto.FilePolicyResponseDto;
import ir.niopdc.policy.dto.ListResponseDto;
import ir.niopdc.policy.facade.PolicyFacade;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
        if (request.getMode() == ModeEnumMessage.CONFIG) {
            sendCompleteBlackList(responseObserver);
        } else {
            sendDifferentialBlackList(request, responseObserver);
        }
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
        FilePolicyResponseDto dto = policyFacade.getGrayListPolicy();
        sendCsvFile(responseObserver, dto);
    }

    private void sendCompleteBlackList(StreamObserver<FilePolicyResponse> responseObserver) throws IOException {
        FilePolicyResponseDto dto = policyFacade.getCompleteBlackList();
        sendBlackListFile(responseObserver, dto);
    }

    private void sendCompleteCodingList(StreamObserver<FilePolicyResponse> responseObserver) throws IOException {
        FilePolicyResponseDto dto = policyFacade.getCompleteCodingList();
        sendCodingListFile(responseObserver, dto);
    }

    private void sendBlackListFile(StreamObserver<FilePolicyResponse> responseObserver, FilePolicyResponseDto dto) throws IOException {
        log.info("Sending file started at {}", LocalDateTime.now());
        responseObserver.onNext(FilePolicyResponse.newBuilder()
                .setMetadata(dto.getMetadata()).build());
        try (Stream<String> stream = Files.lines(dto.getFile(), StandardCharsets.UTF_8)) {
            Spliterator<String> split = stream.spliterator();
            while (true) {
                List<String> chunk = getChunk(split);
                if (chunk.isEmpty()) {
                    break;
                }
                List<BlackListDto> blackListDtos = chunk.stream().map(this::convertBlackListFromCsv).toList();
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);) {
                    objectOutputStream.writeObject(blackListDtos);
                    byte[] fileBytes = outputStream.toByteArray();
                    responseObserver.onNext(FilePolicyResponse.newBuilder().setFile(ByteString.copyFrom(fileBytes)).build());
                } catch (IOException exp) {
                    log.error(exp.getMessage(), exp);
                }
            }
        }
        responseObserver.onCompleted();
        log.info("Sending file ended at {}", LocalDateTime.now());
    }

    private void sendCodingListFile(StreamObserver<FilePolicyResponse> responseObserver, FilePolicyResponseDto dto) throws IOException {
        log.info("Sending file started at {}", LocalDateTime.now());
        responseObserver.onNext(FilePolicyResponse.newBuilder()
                .setMetadata(dto.getMetadata()).build());
        try (Stream<String> stream = Files.lines(dto.getFile(), StandardCharsets.UTF_8)) {
            Spliterator<String> split = stream.spliterator();
            while (true) {
                List<String> chunk = getChunk(split);
                if (chunk.isEmpty()) {
                    break;
                }
                List<CodingDto> codingDtos = chunk.stream().map(this::convertCodingListFromCsv).toList();
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);) {
                    objectOutputStream.writeObject(codingDtos);
                    byte[] fileBytes = outputStream.toByteArray();
                    responseObserver.onNext(FilePolicyResponse.newBuilder().setFile(ByteString.copyFrom(fileBytes)).build());
                } catch (IOException exp) {
                    log.error(exp.getMessage(), exp);
                }
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

    private BlackListDto convertBlackListFromCsv(String csvText) {
        String[] split = csvText.split(appConfig.getCsvDelimiter());
        BlackListDto result = new BlackListDto();
        result.setCardId(split[0]);
        result.setOperation(OperationEnum.getByValue(Byte.parseByte(split[1])));
        return result;
    }

    private CodingDto convertCodingListFromCsv(String csvText) {
        String[] split = csvText.split(appConfig.getCsvDelimiter());
        CodingDto result = new CodingDto();
        result.setCardId(split[0]);
        result.setOperation(OperationEnum.getByValue(Byte.parseByte(split[1])));
        return result;
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

    private void sendDifferentialBlackList(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) throws IOException {
        ListResponseDto listResponseDto = policyFacade.getDifferentialBlackList(request);
        sendDifferentialList(listResponseDto, responseObserver);
    }

    private void sendDifferentialCodingList(PolicyRequest request, StreamObserver<FilePolicyResponse> responseObserver) throws IOException {
        ListResponseDto listResponseDto = policyFacade.getDifferentialCodingList(request);
        sendDifferentialList(listResponseDto, responseObserver);
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
