package com.esun.ecommerce.core.aop;

import com.esun.ecommerce.core.annotation.DbOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class DbAuditAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("DatabaseAuditLogger");

    @Pointcut("@annotation(dbOperation)")
    public void dbOperationPointcut(DbOperation dbOperation) {
    }

    @AfterReturning(pointcut = "dbOperationPointcut(dbOperation)", returning = "result")
    public void logDbOperation(JoinPoint joinPoint, DbOperation dbOperation, Object result) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null) ? authentication.getName() : "SYSTEM";

        String action = dbOperation.action();
        String methodName = joinPoint.getSignature().toShortString();
        String args = Arrays.stream(joinPoint.getArgs())
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        auditLogger.info("使用者: [{}], 操作: [{}], 方法: [{}], 參數: [{}], 結果: [{}]",
                username, action, methodName, args, result);
    }
}
