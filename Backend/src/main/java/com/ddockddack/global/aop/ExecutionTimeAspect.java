package com.ddockddack.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
@Order(1)
@Slf4j
public class ExecutionTimeAspect {
    @Pointcut("execution(* com.ddockddack.domain..*Controller.*(..))") // 이런 패턴이 실행될 경우 수행
    public void timer() {
    }

    @Around("timer()")
    public Object executionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        StopWatch sw = new StopWatch();

        sw.start();
        Object result = proceedingJoinPoint.proceed();
        sw.stop();

        long executionTime = sw.getTotalTimeMillis();
        String methodName = proceedingJoinPoint.getSignature().getName();

        log.info("[ExecutionTime] " + methodName + " --> " + executionTime + "ms");

        return result;
    }

}
