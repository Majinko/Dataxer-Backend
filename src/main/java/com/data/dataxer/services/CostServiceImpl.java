package com.data.dataxer.services;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.enums.CostState;
import com.data.dataxer.repositories.CostRepository;
import com.data.dataxer.repositories.qrepositories.QCostRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.MandatoryValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CostServiceImpl implements CostService {

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
    public Cost update(Cost oldCost) {
        return this.qCostRepository.getByIdWithRelation(oldCost.getId(), SecurityUtils.companyId()).map(cost -> {

            cost.setContact(oldCost.getContact());
            cost.setProject(oldCost.getProject());
            cost.setTitle(oldCost.getTitle());
            cost.setCostOrder(oldCost.getCostOrder());
            cost.setCategories(oldCost.getCategories());
            cost.setNumber(oldCost.getNumber());
            cost.setVariableSymbol(oldCost.getVariableSymbol());
            cost.setConstantSymbol(oldCost.getConstantSymbol());
            cost.setCurrency(oldCost.getCurrency());
            cost.setNote(oldCost.getNote());
            cost.setState(oldCost.getState());
            cost.setType(oldCost.getType());
            cost.setPeriod(oldCost.getPeriod());
            cost.setPaymentMethod(oldCost.getPaymentMethod());
            cost.setIsInternal(oldCost.getIsInternal());
            cost.setIsRepeated(oldCost.getIsRepeated());
            cost.setCostData(oldCost.getCostData());
            cost.setPrice(oldCost.getPrice());
            cost.setTax(oldCost.getTax());
            cost.setTotalPrice(oldCost.getTotalPrice());
            cost.setPaymentDate(oldCost.getPaymentDate());
            cost.setRepeatedFrom(oldCost.getRepeatedFrom());
            cost.setRepeatedTo(oldCost.getRepeatedTo());
            cost.setNextRepeatedCost(oldCost.getNextRepeatedCost());
            cost.setCreatedDate(oldCost.getCreatedDate());
            cost.setDueDate(oldCost.getDueDate());
            cost.setDeliveredDate(oldCost.getDeliveredDate());
            cost.setTaxableSupply(oldCost.getTaxableSupply());

            costRepository.save(cost);

            return cost;
        }).orElse(null);
    }

    @Override
    public Page<Cost> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qCostRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyId());
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
        Cost oldCost = this.qCostRepository.getById(id, SecurityUtils.companyId())
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
        return this.qCostRepository.getById(id, SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Cost not found"));
    }

    @Override
    public Cost getByIdWithRelation(Long id) {
        return this.qCostRepository.getByIdWithRelation(id, SecurityUtils.companyId()).orElse(null);
    }

    @Override
    public Cost duplicate(Long id) {
        Cost oldCost = this.qCostRepository.getById(id, SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Cost not found"));
        Cost newCost = new Cost();
        BeanUtils.copyProperties(oldCost, newCost, "id");
        return this.store(newCost);
    }

    @Override
    public List<Cost> findAllByProject(Long projectId) {
        return costRepository.findAllByProjectIdAndCompanyId(projectId, SecurityUtils.companyId());
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
