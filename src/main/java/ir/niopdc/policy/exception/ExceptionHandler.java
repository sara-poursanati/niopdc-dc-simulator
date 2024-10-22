package ir.niopdc.policy.exception;

import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
@Slf4j
public class ExceptionHandler {

    @GrpcExceptionHandler(Exception.class)
    public Status handleException(Exception exp) {
        log.error(exp.getMessage(), exp);
        return Status.INTERNAL.withDescription("Server internal error").withCause(exp);
    }
}
