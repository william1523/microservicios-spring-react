package com.will1523.login.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.will1523.login.controller..*(..)) || execution(* com.will1523.login.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        logger.info("Entering {}.{} with arguments: {}", className, methodName, Arrays.toString(joinPoint.getArgs()));

        long start = System.currentTimeMillis();
        try {
            Object proceed = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            logger.info("Exiting {}.{} with result: {} (Execution time: {} ms)", className, methodName, proceed, executionTime);
            return proceed;
        } catch (Throwable e) {
            long executionTime = System.currentTimeMillis() - start;
            logger.error("Exception in {}.{}: {} (Execution time: {} ms)", className, methodName, e.getMessage(), executionTime);
            throw e;
        }
    }
}
