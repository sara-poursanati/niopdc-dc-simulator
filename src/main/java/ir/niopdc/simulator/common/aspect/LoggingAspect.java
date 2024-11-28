package ir.niopdc.simulator.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    @Pointcut("within(ir.niopdc..*)")
    public void inNiopdc() {}

    @Pointcut("publicMethod() && inNiopdc()")
    public void niopdcOperation() {}

    @Before(value = "niopdcOperation()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        log.info("Method execution started {}() - {}", methodName, Arrays.toString(args));
    }
}
