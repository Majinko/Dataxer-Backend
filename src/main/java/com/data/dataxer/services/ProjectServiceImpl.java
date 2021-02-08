package com.data.dataxer.services;

import com.data.dataxer.models.domain.Project;
import com.data.dataxer.repositories.ProjectRepository;
import com.data.dataxer.repositories.qrepositories.QProjectRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final QProjectRepository qProjectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, QProjectRepository qProjectRepository) {
        this.projectRepository = projectRepository;
        this.qProjectRepository = qProjectRepository;
    }

    @Override
    public Project store(Project project) {
        return this.projectRepository.save(project);
    }

    @Override
    public Project getById(Long id, Boolean disableFilter) {
        return this.qProjectRepository.getById(id, SecurityUtils.CompanyId(), disableFilter);
    }

    @Override
    public Project update(Project project) {
        return this.projectRepository.save(project);
    }

    @Override
    public Page<Project> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter) {
        return qProjectRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.CompanyId(), disableFilter);
    }

    @Override
    public void destroy(Long id) {
        this.projectRepository.delete(this.qProjectRepository.getById(id, SecurityUtils.CompanyId(), false));
    }

    @Override
    public List<Project> all() {
        return this.projectRepository.findAllByCompanyIdIn(SecurityUtils.companyIds());
    }

    @Override
    public List<Project> search(String queryString, Boolean disableFilter) {
        return this.qProjectRepository.search(queryString, SecurityUtils.CompanyId(),disableFilter);
    }
}
