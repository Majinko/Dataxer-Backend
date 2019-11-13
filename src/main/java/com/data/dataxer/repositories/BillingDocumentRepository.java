package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.BillingDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingDocumentRepository extends JpaRepository<BillingDocument, Long> {
}
