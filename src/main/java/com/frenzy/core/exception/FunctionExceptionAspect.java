package com.frenzy.core.exception;


import com.microsoft.azure.functions.HttpRequestMessage;
import org.apache.xmlbeans.InterfaceExtension;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class FunctionExceptionAspect {

    private final GlobalExceptionHandler globalExceptionHandler;

    public FunctionExceptionAspect(GlobalExceptionHandler globalExceptionHandler) {
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @Around("@annotation(com.microsoft.azure.functions.annotation.FunctionName)")
    public Object handleException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
//            Method method = ((InterfaceExtension.MethodSignature) joinPoint.getSignature()).getMethod();
//            HttpRequestMessage<?> request = (HttpRequestMessage<?>) joinPoint.getArgs()[0];
            return globalExceptionHandler.handleGlobalException(ex);
        }
    }
}

