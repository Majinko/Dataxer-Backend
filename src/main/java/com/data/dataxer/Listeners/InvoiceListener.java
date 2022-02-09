package com.data.dataxer.Listeners;

import com.data.dataxer.models.domain.AuditLog;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.AuditLogAction;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.services.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class InvoiceListener {

    @PrePersist
    public void prePersist(Invoice invoice) {
        logAction(invoice, AuditLogAction.INSERT);
    }

    @PreUpdate
    public void preUpdate(Invoice invoice) {
        logAction(invoice, AuditLogAction.UPDATE);
    }

    @PreRemove
    public void preRemove(Invoice invoice) {
        logAction(invoice, AuditLogAction.DELETE);
    }

    @Transactional
    protected void logAction(Invoice invoice, AuditLogAction action) {
        ObjectMapper objectMapper = new ObjectMapper();
        EntityManager entityManager = BeanUtil.getBean(EntityManager.class);

        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setObjectId(invoice.getId());
            auditLog.setAuditLogAction(action);
            auditLog.setLoggedObject(objectMapper.writeValueAsString(invoice));
            auditLog.setInitiator(SecurityUtils.loggedUser());

            entityManager.persist(auditLog);
        } catch (JsonProcessingException e) {
        }
    }

}
