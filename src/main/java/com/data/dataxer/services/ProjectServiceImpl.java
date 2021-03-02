package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.repositories.ProjectRepository;
import com.data.dataxer.repositories.qrepositories.QProjectRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public Project getById(Long id) {
        return this.qProjectRepository.getById(id, SecurityUtils.companyId());
    }

    @Override
    public void update(Project project) {
        this.projectRepository.save(project);
    }

    @Override
    public Page<Project> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return qProjectRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyId());
    }

    @Override
    public void destroy(Long id) {
        this.projectRepository.delete(this.qProjectRepository.getById(id, SecurityUtils.companyId()));
    }

    @Override
    public List<Project> all() {
        return this.projectRepository.findAllByCompanyId(SecurityUtils.companyId());
    }

    @Override
    public List<Project> search(String queryString) {
        return this.qProjectRepository.search(SecurityUtils.companyId(), queryString);
    }

    @Override
    public List<Category> getAllProjectCategories(Long projectId) {
        return this.qProjectRepository.getById(projectId, SecurityUtils.companyId()).getCategories();
    }

    @Override
    public List<Category> getAllProjectCategoriesOrderedByPosition(Long projectId) {
        List<Category> categories = this.qProjectRepository.getById(projectId, SecurityUtils.companyId()).getCategories();
        Collections.sort(categories, Comparator.comparing(Category::getLft));
       // Collections.sort(categories, Comparator.comparing(Category::getDepth).thenComparing(Category::getPosition));

        return categories;
    }
}
