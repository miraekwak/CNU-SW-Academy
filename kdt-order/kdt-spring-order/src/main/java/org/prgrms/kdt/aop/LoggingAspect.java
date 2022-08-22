package org.prgrms.kdt.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    //@Around("org.prgrms.kdt.aop.CommonPointcut.repositoryInsertMethodPointcut()")
    @Around("@annotation(org.prgrms.kdt.aop.TrackTime)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Before method called. {}", joinPoint.getSignature().toString());
        var startTime = System.nanoTime(); // 1 s -> 1000 ms 1 ms -> 1000 mcs 1 mcs -> 1000 ns
        var result = joinPoint.proceed();
        var endTime = System.nanoTime() - startTime;
        log.info("After method calld with result => {} and time taken {} nanoseconds", result, endTime);
        return result;

    }
}
