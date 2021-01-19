package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.enums.CostState;
import com.data.dataxer.repositories.CostRepository;
import com.data.dataxer.repositories.qrepositories.QCostRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.MandatoryValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CostServiceImpl implements CostService{

    private final CostRepository costRepository;
    private final QCostRepository qCostRepository;

    public CostServiceImpl(CostRepository costRepository, QCostRepository qCostRepository) {
        this.costRepository = costRepository;
        this.qCostRepository = qCostRepository;
    }

    @Override
    public Cost store(Cost cost) {
        if (cost.getIsRepeated()) {
            MandatoryValidator.validateRepeatedCostMandatory(cost);
            if (cost.getRepeatedFrom() == null) {
                cost.setRepeatedFrom(LocalDate.now());
            }
            Cost fromRepeatedCost = this.generateNewCostFromRepeated(cost);
            cost.setNextRepeatedCost(this.getNextRepeat(cost));
            this.costRepository.save(fromRepeatedCost);
        }
        return this.costRepository.save(cost);
    }

    @Override
    public Cost update(Cost cost) {
        return this.costRepository.save(cost);
    }

    @Override
    public Page<Cost> paginate(Pageable pageable, List<Filter> filters) {
        List<Cost> costs;
        String whereCondition;
        if (!filters.isEmpty()) {
            whereCondition = Filter.buildConditionsFromFilters(filters, "c");
            costs = this.costRepository.findWithFilter(SecurityUtils.companyIds(), whereCondition, pageable);
        } else {
            costs = this.costRepository.findDefault(SecurityUtils.companyIds(), pageable);
        }

        return new PageImpl<>(costs, pageable, costs.size());
    }

    @Override
    public void taskExecute() {
        LocalDate currentDay = LocalDate.now();
        List<Cost> repeatedCosts = this.costRepository.findAllRepeated();
        for (Cost repeatedCost : repeatedCosts) {
            if (repeatedCost.getNextRepeatedCost().toEpochDay() == currentDay.toEpochDay()) {
                this.costRepository.save(this.generateNewCostFromRepeated(repeatedCost));
            }
            LocalDate nextRepeat = getNextRepeat(repeatedCost);
            if (nextRepeat != null) {
                repeatedCost.setNextRepeatedCost(nextRepeat);
            }
        }
    }

    @Override
    public Cost changeState(Long id, CostState state) {
        Cost oldCost = this.qCostRepository.getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Cost not found"));
        oldCost.setState(state);
        return this.update(oldCost);
    }

    @Override
    public void destroy(Long id) {
        this.costRepository.delete(this.getById(id));
    }

    @Override
    public Cost getById(Long id) {
        return this.qCostRepository.getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Cost not found"));
    }

    @Override
    public Cost duplicate(Long id) {
        Cost oldCost = this.qCostRepository.getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Cost not found"));
        Cost newCost = new Cost();
        BeanUtils.copyProperties(oldCost, newCost, "id");
        return this.store(newCost);
    }

    private Cost generateNewCostFromRepeated(Cost repeatedCost) {
        Cost newCost = new Cost();
        BeanUtils.copyProperties(repeatedCost, newCost, "id", "dueDate", "createdDate", "isRepeated", "state");
        newCost.setCreatedDate(LocalDate.now());
        if (repeatedCost.getDueDate() != null) {
            newCost.setDueDate(repeatedCost.getDueDate());
        } else {
            newCost.setDueDate(LocalDate.now());
        }
        return newCost;
    }

    private LocalDate getNextRepeat(Cost cost) {
        LocalDate nextRepeatedCost = cost.getNextRepeatedCost();
        if (nextRepeatedCost != null) {
            switch (cost.getPeriod()) {
                case DAY:
                    nextRepeatedCost = nextRepeatedCost.plusDays(1);
                    break;
                case WEEK:
                    nextRepeatedCost = nextRepeatedCost.plusWeeks(1);
                    break;
                case MONTH:
                    nextRepeatedCost = nextRepeatedCost.plusMonths(1);
                    break;
                case YEAR:
                    nextRepeatedCost = nextRepeatedCost.plusYears(1);
                    break;
            }
            if (nextRepeatedCost.toEpochDay() > cost.getRepeatedTo().toEpochDay()) {
                this.costRepository.deleteById(cost.getId());
                return null;
            }
            return nextRepeatedCost;
        }
        return LocalDate.now();
    }
}
