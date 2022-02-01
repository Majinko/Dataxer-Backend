package com.data.dataxer.auditLog;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

@Component
@Aspect
public class AuditLogAspect {

    private Logger logger = LoggerFactory.getLogger(AuditLogLogger.class.getName());


    @Pointcut("@target(org.springframework.stereotype.Repository)")
    public void repositoryMethods() {};

}
