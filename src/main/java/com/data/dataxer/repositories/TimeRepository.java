package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.domain.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TimeRepository extends JpaRepository<Time, Long> {
    List<Time> findByCategoryIdIn(List<Long> ids);

    Time findFirstByUserIdAndCompanyIdAndDateWorkOrderByIdDesc(Long userId, Long companyId, LocalDate dateWork);

    List<Time> findAllBySalaryIdAndAndCompanyId(Long salaryId, Long companyId);

    @Query("select t from Time t left join fetch t.category where t.company.id = ?1 and t.user.uid = ?2")
    List<Time> findAllByCompanyIdAndUserUid(Long companyId, String userId);

    @Query(value = "SELECT t.project_id FROM " +
            "(SELECT DISTINCT ON (project_id) project_id, id " +
            "FROM time " +
            "WHERE uid = ?1 AND company_id = ?3 " +
            "ORDER BY project_id, id DESC) t " +
            "ORDER BY t.id DESC LIMIT ?2", nativeQuery = true)
    List<Long> loadLastUserProject(String uid, Long limit, Long companyId);


    @Query(value = "SELECT t.category_id FROM " +
            "(SELECT DISTINCT ON (category_id) category_id, id " +
            "FROM time " +
            "WHERE project_id = ?1 AND uid = ?2 AND company_id = ?3 " +
            "ORDER BY category_id, id DESC) t " +
            "ORDER BY t.id DESC LIMIT ?4", nativeQuery = true)
    List<Long> loadLastUserCategories(Long projectId, String uid, Long companyId, Long limit);
}
