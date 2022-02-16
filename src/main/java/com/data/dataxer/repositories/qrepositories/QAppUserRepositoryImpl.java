package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.security.model.Privilege;
import com.data.dataxer.security.model.QPrivilege;
import com.data.dataxer.security.model.QRole;
import com.data.dataxer.security.model.Role;
import com.data.dataxer.utils.Helpers;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
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
    public List<AppUser> findWhereDefaultProfileId(Long appProfileId) {
        return this.query.selectFrom(QAppUser.appUser)
                .where(QAppUser.appUser.defaultProfile.id.eq(appProfileId))
                .fetch();
    }

    @Override
    public Optional<AppUser> findByUid(String uid) {
        AppUser appUser = this.query.selectFrom(QAppUser.appUser)
                .leftJoin(QAppUser.appUser.defaultProfile).fetchJoin()
                .leftJoin(QAppUser.appUser.roles).fetchJoin()
                .where(QAppUser.appUser.uid.eq(uid))
                .fetchOne();

        if (appUser != null) {
            appUserSetRolePrivileges(appUser);
        }
        return Optional.ofNullable(appUser);
    }

    @Override
    public Optional<AppUser> findUserWithRolesAndPrivileges(String uid, Long appProfileId) {
        return Optional.ofNullable(
                query.selectFrom(QAppUser.appUser)
                        .leftJoin(QAppUser.appUser.roles, QRole.role).fetchJoin()
                        .where(QAppUser.appUser.uid.eq(uid))
                        .where(QAppUser.appUser.defaultProfile.id.eq(appProfileId))
                        .fetchOne()
        );
    }

    @Override
    public List<AppUser> getUsersByAppProfileId(Pageable pageable, String qString, Long appProfileId) {
        List<AppUser> appUsers = this.query
                .selectFrom(QAppUser.appUser)
                .leftJoin(QAppUser.appUser.overviewData, QUsersOverviewData.usersOverviewData).fetchJoin()
                .where(search(qString))
                .where(QAppUser.appUser.id.in(this.userIds(appProfileId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(QAppUser.appUser.isDisabled.eq(false))
                .orderBy(QAppUser.appUser.isDisabled.asc())
                .fetch();


        // ger roles by users
        List<Role> roles = this.query.selectFrom(QRole.role)
                .leftJoin(QRole.role.users, QAppUser.appUser).fetchJoin()
                .where(QAppUser.appUser.in(appUsers))
                .distinct()
                .fetch();

        // users set roles
        appUsers.forEach(user -> {
            user.setRoles(
                    roles.stream().filter(role -> role.getUsers().contains(user)).collect(Collectors.toList())
            );
        });

        return appUsers;
    }

    @Override
    public List<AppUser> getUsersByAppProfileId(Long appProfileId) {
        return this.query
                .selectFrom(QAppUser.appUser)
                .where(QAppUser.appUser.id.in(this.userIds(appProfileId)))
                .fetch();
    }

    private JPQLQuery<String> queryForGetActiveUser() {
        return JPAExpressions
                .select(QTime.time1.user.uid)
                .from(QTime.time1)
                .where(QTime.time1.dateWork.goe(Helpers.getLastDate(2)))
                .orderBy(QTime.time1.timeFrom.desc())
                .fetchAll();
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

    private Set<Long> userIds(Long appProfileId) {
        Set<Long> userIds = new HashSet<>();

        List<AppProfile> profiles = this.query
                .selectFrom(QAppProfile.appProfile)
                .where(QAppProfile.appProfile.id.in(appProfileId))
                .leftJoin(QAppProfile.appProfile.appUsers, QAppUser.appUser).fetchJoin()
                .distinct()
                .fetch();

        profiles.forEach(profile -> {
            userIds.addAll(profile.getAppUsers().stream().map(AppUser::getId).collect(Collectors.toList()));
        });

        return userIds;
    }
}
