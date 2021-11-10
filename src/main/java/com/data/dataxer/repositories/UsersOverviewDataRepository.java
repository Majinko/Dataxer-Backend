package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.UsersOverviewData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersOverviewDataRepository extends JpaRepository<UsersOverviewData, Long> {

    List<UsersOverviewData> findAllByCompanyId(Long companyId);

    UsersOverviewData findByUserUidAndYearAndMonthAndCompanyId(String uid, Integer year, Integer month, Long companyId);

}
