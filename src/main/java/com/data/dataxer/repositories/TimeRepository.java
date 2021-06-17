package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TimeRepository extends JpaRepository<Time, Long> {
    List<Time> findByCategoryIdIn(List<Long> ids);

    Time findFirstByUserIdAndCompanyIdAndDateWorkOrderByIdDesc(Long userId, Long companyId, LocalDate dateWork);

    List<Time> findAllBySalaryIdAndAndCompanyId(Long salaryId, Long companyId);

    List<Time> findAllByCompanyId(Long companyId);

    @Query("select t from Time t left join fetch t.category where t.company.id = ?1 and t.user.uid = ?2")
    List<Time> findAllByCompanyIdAndUserUid(Long companyId, String userId);
}
