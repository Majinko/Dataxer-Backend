package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.QAppUser;
import com.data.dataxer.models.domain.QCompany;
import com.data.dataxer.security.model.Privilege;
import com.data.dataxer.security.model.QPrivilege;
import com.data.dataxer.security.model.QRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class QAppUserRepositoryImpl implements QAppUserRepository {
    private final JPAQueryFactory query;

    public QAppUserRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<AppUser> all(List<Long> companyIds) {
        QAppUser qAppUser = QAppUser.appUser;
        QCompany qCompany = QCompany.company;

        /*return query.selectFrom(qAppUser)
                .where(qAppUser.id.in(
                        JPAExpressions.select(qCompany).where(qCompany.id.in(companyIds))
                ))
                .fetch();*/
        return null;
    }

    @Override
    public List<AppUser> findWhereDefaultCompanyIs(Long companyId) {
        return this.query.selectFrom(QAppUser.appUser)
                .where(QAppUser.appUser.defaultCompany.id.eq(companyId))
                .fetch();
    }

    @Override
    public Optional<AppUser> findByUid(String uid) {
        AppUser appUser = this.query.selectFrom(QAppUser.appUser)
                .leftJoin(QAppUser.appUser.defaultCompany).fetchJoin()
                .leftJoin(QAppUser.appUser.roles).fetchJoin()
                .where(QAppUser.appUser.uid.eq(uid))
                .fetchOne();

        if (appUser != null) {
            appUserSetRolePrivileges(appUser);
        }
        return Optional.ofNullable(appUser);
        /*return Optional.ofNullable(
                this.query.selectFrom(QAppUser.appUser)
                    .leftJoin(QAppUser.appUser.defaultCompany).fetchJoin()
                    .leftJoin(QAppUser.appUser.roles).fetchJoin()
                    .leftJoin(QAppUser.appUser.roles)
                    .where(QAppUser.appUser.uid.eq(uid))
                    .fetchOne()
        );*/
    }

    private void appUserSetRolePrivileges(AppUser appUser) {
        appUser.getRoles().forEach(role -> {
            List<Privilege> privileges = this.query.selectFrom(QPrivilege.privilege)
                    .where(QPrivilege.privilege.roles.contains(role))
                    .fetch();
            role.setPrivileges(privileges);
        });
    }
}
