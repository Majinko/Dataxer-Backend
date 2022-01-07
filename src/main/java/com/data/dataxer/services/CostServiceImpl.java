package com.data.dataxer.services;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.domain.Payment;
import org.springframework.data.domain.Page;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.CostRepository;
import com.data.dataxer.repositories.qrepositories.QCostRepository;
import com.data.dataxer.repositories.qrepositories.QPaymentRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.MandatoryValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class CostServiceImpl implements CostService {
    @Autowired
    private CostRepository costRepository;

    @Autowired
    private QCostRepository qCostRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private QPaymentRepository qPaymentRepository;

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

        Cost newCost = this.costRepository.save(cost);

        if (newCost.getState() == DocumentState.PAYED) {
            this.storeCostPayment(newCost); // todo tpm odstranit ked sa opravia platby pri nakladoch
        }

        return newCost;
    }

    @Override
    public Cost update(Cost oldCost) {
        this.checkCostPayment(oldCost);

        return this.qCostRepository.getByIdWithRelation(oldCost.getId(), SecurityUtils.companyIds()).map(cost -> {
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

    private void checkCostPayment(Cost cost) {
        BigDecimal payedTotalPrice = this.qPaymentRepository.getPayedTotalPrice(cost.getId(), DocumentType.COST);

        boolean isPayed = cost.getTotalPrice().subtract(payedTotalPrice).setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) == 0;

        if (!isPayed) {
            cost.setPaymentDate(null);
            cost.setState(DocumentState.UNPAID);
        } else {
            cost.setState(DocumentState.PAYED);
            cost.setPaymentDate(LocalDate.now());
        }
    }

    @Override
    public Page<Cost> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qCostRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyIds());
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
    public Cost changeState(Long id, DocumentState state) {
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
    public Cost getByIdWithRelation(Long id) {
        return this.qCostRepository.getByIdWithRelation(id, SecurityUtils.companyIds()).orElse(null);
    }

    @Override
    public Cost duplicate(Long id) {
        Cost oldCost = this.qCostRepository.getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Cost not found"));
        Cost newCost = new Cost();
        BeanUtils.copyProperties(oldCost, newCost, "id");
        return this.store(newCost);
    }

    @Override
    public List<Cost> findAllByProject(Long projectId, List<Long> companyIds) {
        return costRepository.findAllByProjectIdAndCompanyIdIn(projectId, SecurityUtils.companyIds(companyIds));
    }

    @Override
    public List<Integer> getCostsYears() {
        List<Integer> years = this.qCostRepository.getCostsYears(SecurityUtils.companyId());

        years.sort(Collections.reverseOrder());

        return years;
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

    private void storeCostPayment(Cost cost) {
        Payment payment = new Payment();

        payment.setPaymentMethod(cost.getPaymentMethod());
        payment.setDocumentId(cost.getId());
        payment.setPayedValue(cost.getPrice());
        payment.setDocumentType(DocumentType.COST);
        payment.setPayedDate(cost.getPaymentDate());

        this.paymentService.store(payment);
    }
}
