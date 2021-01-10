package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.MailAccounts;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class QMailAccountsRepositoryImpl implements QMailAccountsRepository {

    private final JPAQueryFactory query;

    public QMailAccountsRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<MailAccounts> getById(Long id, List<Long> companyIds) {
        return null;
    }

    @Override
    public long updateByMailAccounts(MailAccounts mailAccounts, List<Long> companyIds) {
        return 0;
    }

    @Override
    public Page<MailAccounts> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds) {
        return null;
    }

    @Override
    public Optional<MailAccounts> getByCompaniesId(Long companyId) {
        return Optional.empty();
    }
}
