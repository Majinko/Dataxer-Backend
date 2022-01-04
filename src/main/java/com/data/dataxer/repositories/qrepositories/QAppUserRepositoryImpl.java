package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.security.model.Privilege;
import com.data.dataxer.security.model.QPrivilege;
import com.data.dataxer.security.model.QRole;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

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
    }

    @Override
    public Optional<AppUser> findUserWithRolesAndPrivileges(String uid, Long companyId) {
        return Optional.ofNullable(
                query.selectFrom(QAppUser.appUser)
                        .leftJoin(QAppUser.appUser.roles, QRole.role).fetchJoin()
                        .where(QAppUser.appUser.uid.eq(uid))
                        .where(QAppUser.appUser.defaultCompany.id.eq(companyId))
                        .fetchOne()
        );
    }

    @Override
    public List<AppUser> getUsersByCompany(Pageable pageable, String qString, List<Long> companyIds) {
        Set<Long> userIds = new HashSet<>();

        List<Company> companies = this.query
                .selectFrom(QCompany.company)
                .where(QCompany.company.id.in(companyIds))
                .leftJoin(QCompany.company.appUsers, QAppUser.appUser).fetchJoin()
                .distinct()
                .fetch();

        companies.forEach(c -> {
            userIds.addAll(c.getAppUsers().stream().map(AppUser::getId).collect(Collectors.toList()));
        });

        return this.query
                .selectFrom(QAppUser.appUser)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(search(qString))
                .where(QAppUser.appUser.id.in(userIds))
          /*      .where(QAppUser.appUser.uid.in(
                        JPAExpressions
                                .select(QTime.time1.user.uid)
                                .where(QTime.time1.dateWork.goe())
                                .orderBy(QTime.time1.timeFrom.desc())
                                .fetchAll()
                ))*/
                .fetch();
    }

    private void appUserSetRolePrivileges(AppUser appUser) {
        appUser.getRoles().forEach(role -> {
            List<Privilege> privileges = this.query.selectFrom(QPrivilege.privilege)
                    .where(QPrivilege.privilege.roles.contains(role))
                    .fetch();
            role.setPrivileges(privileges);
        });
    }

    private Long getTotalCount(String qString) {
        return this.query
                .selectFrom(QAppUser.appUser)
                .where(search(qString))
                .fetchCount();
    }

    private BooleanBuilder search(String queryString) {
        BooleanBuilder where = new BooleanBuilder();

        if (queryString != null)
            where
                    .or(QAppUser.appUser.firstName.containsIgnoreCase(queryString))
                    .or(QAppUser.appUser.email.containsIgnoreCase(queryString));

        return where;
    }
}
