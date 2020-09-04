package com.data.dataxer.filters;

import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.domain.PriceOffer;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.models.enums.Periods;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Filter {

    public static final String GREATER_THAN = "GT";
    public static final String EQUAL_GREATER_THAN = "EGT";
    public static final String LOWER_THAN = "LT";
    public static final String EQUAL_LOWER_THAN = "ELT";
    public static final String EQUAL = "E";
    public static final String BETWEEN = "BT";
    public static final String NOT_EQUAL = "NE";
    public static final String LIKE = "L";

    private String columnId = "";
    private String operator = "";
    private List<String> values = new ArrayList<>();

    private Ops resolveOperator() {
        switch (this.operator) {
            case GREATER_THAN:
                return Ops.GT;
            case EQUAL_GREATER_THAN:
                return Ops.GOE;
            case LOWER_THAN:
                return Ops.LT;
            case EQUAL_LOWER_THAN:
                return Ops.LOE;
            case BETWEEN:
                return Ops.BETWEEN;
            case LIKE:
                return Ops.LIKE;
            case NOT_EQUAL:
                return Ops.NE;
            case EQUAL:
            default:
                return Ops.EQ;
        }
    }

    public boolean isEmpty() {
        return this.columnId.isEmpty() && this.operator.isEmpty() && this.values.isEmpty();
    }

    public BooleanBuilder buildInvoiceFilterPredicate() throws RuntimeException {
        Path<Invoice> invoice = Expressions.path(Invoice.class, "invoice");
        BooleanBuilder builder = new BooleanBuilder();

        if (SearchableStrings.isSearchableString(this.columnId)) {
            Path<String> stringBase = Expressions.path(String.class, invoice, this.columnId);
            Expression<String> value = Expressions.constant(this.values.get(0));
            return builder.or(Expressions.predicate(resolveOperator(), stringBase, value));
        }
        if (SearchableDecimals.isSearchableDecimal(this.columnId)) {
            Path<BigDecimal> bigDecimalBase = Expressions.path(BigDecimal.class, invoice, this.columnId);
            Expression<BigDecimal> value = Expressions.constant(new BigDecimal(this.values.get(0)));
            if (this.values.size() > 1) {
                Expression<BigDecimal> value2 = Expressions.constant(new BigDecimal(this.values.get(1)));
                return builder.or(Expressions.predicate(resolveOperator(), bigDecimalBase, value, value2));
            }
            return builder.or(Expressions.predicate(resolveOperator(), bigDecimalBase, value));
        }
        if (SearchableDates.isSearchableDate(this.columnId)) {
            Path<LocalDate> dateBase = Expressions.path(LocalDate.class, invoice, this.columnId);
            Expression<LocalDate> value = Expressions.constant(LocalDate.parse(this.values.get(0)));
            if (this.values.size() > 1) {
                Expression<LocalDate> value2 = Expressions.constant(LocalDate.parse(this.values.get(1)));
                return builder.or(Expressions.predicate(resolveOperator(), dateBase, value, value2));
            }
            return builder.or(Expressions.predicate(resolveOperator(), dateBase, value));
        }
        if (this.columnId.equals("state")) {
            Path<DocumentState.InvoiceStates> stateBase = Expressions.path(DocumentState.InvoiceStates.class, invoice, this.columnId);
            if (this.values.size() > 1) {
                for (String state : this.values) {
                    Expression<DocumentState.InvoiceStates> value = Expressions.constant(DocumentState.InvoiceStates.getStateByCode(state));
                    builder.or(Expressions.predicate(Ops.EQ, stateBase, value));
                }
                return builder;
            }
            Expression<DocumentState.InvoiceStates> value = Expressions.constant(DocumentState.InvoiceStates.getStateByCode(this.values.get(0)));
            return builder.or(Expressions.predicate(resolveOperator(), stateBase, value));
        }
        throw new RuntimeException("Not valid filter!");
    }

    public BooleanBuilder buildPriceOfferFilterPredicate() throws RuntimeException {
        Path<PriceOffer> priceOffer = Expressions.path(PriceOffer.class, "priceOffer");
        BooleanBuilder builder = new BooleanBuilder();

        if (SearchableStrings.isSearchableString(this.columnId)) {
            Path<String> stringBase = Expressions.path(String.class, priceOffer, this.columnId);
            Expression<String> value = Expressions.constant(this.values.get(0));
            return builder.or(Expressions.predicate(resolveOperator(), stringBase, value));
        }
        if (SearchableDecimals.isSearchableDecimal(this.columnId)) {
            Path<BigDecimal> bigDecimalBase = Expressions.path(BigDecimal.class, priceOffer, this.columnId);
            Expression<BigDecimal> value = Expressions.constant(new BigDecimal(this.values.get(0)));
            if (this.values.size() > 1) {
                Expression<BigDecimal> value2 = Expressions.constant(new BigDecimal(this.values.get(1)));
                return builder.or(Expressions.predicate(resolveOperator(), bigDecimalBase, value, value2));
            }
            return builder.or(Expressions.predicate(resolveOperator(), bigDecimalBase, value));
        }
        if (SearchableDates.isSearchableDate(this.columnId)) {
            Path<LocalDate> dateBase = Expressions.path(LocalDate.class, priceOffer, this.columnId);
            Expression<LocalDate> value = Expressions.constant(LocalDate.parse(this.values.get(0)));
            if (this.values.size() > 1) {
                Expression<LocalDate> value2 = Expressions.constant(LocalDate.parse(this.values.get(1)));
                return builder.or(Expressions.predicate(resolveOperator(), dateBase, value, value2));
            }
            return builder.or(Expressions.predicate(resolveOperator(), dateBase, value));
        }
        if (this.columnId.equals("state")) {
            Path<DocumentState.InvoiceStates> stateBase = Expressions.path(DocumentState.InvoiceStates.class, priceOffer, this.columnId);
            if (this.values.size() > 1) {
                for (String state : this.values) {
                    Expression<DocumentState.InvoiceStates> value = Expressions.constant(DocumentState.InvoiceStates.getStateByCode(state));
                    builder.or(Expressions.predicate(Ops.EQ, stateBase, value));
                }
                return builder;
            }
            Expression<DocumentState.InvoiceStates> value = Expressions.constant(DocumentState.InvoiceStates.getStateByCode(this.values.get(0)));
            return builder.or(Expressions.predicate(resolveOperator(), stateBase, value));
        }
        throw new RuntimeException("Not valid filter!");
    }

    public BooleanBuilder buildNumberGeneratorFilterPredicate() throws RuntimeException {
        Path<DocumentNumberGenerator> documentNumberGenerator = Expressions.path(DocumentNumberGenerator.class, "documentNumberGenerator");
        BooleanBuilder builder = new BooleanBuilder();

        if (SearchableStrings.isSearchableString(this.columnId)) {
            Path<String> stringBase = Expressions.path(String.class, documentNumberGenerator, this.columnId);
            Expression<String> value = Expressions.constant(this.values.get(0));
            return builder.or(Expressions.predicate(resolveOperator(), stringBase, value));
        }
        if (SearchableDecimals.isSearchableDecimal(this.columnId)) {
            Path<BigDecimal> bigDecimalBase = Expressions.path(BigDecimal.class, documentNumberGenerator, this.columnId);
            Expression<BigDecimal> value = Expressions.constant(new BigDecimal(this.values.get(0)));
            if (this.values.size() > 1) {
                Expression<BigDecimal> value2 = Expressions.constant(new BigDecimal(this.values.get(1)));
                return builder.or(Expressions.predicate(resolveOperator(), bigDecimalBase, value, value2));
            }
            return builder.or(Expressions.predicate(resolveOperator(), bigDecimalBase, value));
        }
        if (this.columnId.equals("type")) {
            Path<DocumentType> typeBase = Expressions.path(DocumentType.class, documentNumberGenerator, this.columnId);
            if (this.values.size() > 1) {
                for (String type : this.values) {
                    Expression<DocumentType> value = Expressions.constant(DocumentType.getTypeByName(type));
                    builder.or(Expressions.predicate(Ops.EQ, typeBase, value));
                }
                return builder;
            }
            Expression<DocumentType> value = Expressions.constant(DocumentType.getTypeByName(this.values.get(0)));
            return builder.or(Expressions.predicate(resolveOperator(), typeBase, value));
        }
        if (this.columnId.equals("period")) {
            Path<Periods> periodBase = Expressions.path(Periods.class, documentNumberGenerator, this.columnId);
            if (this.values.size() > 1) {
                for (String period : this.values) {
                    Expression<Periods> value = Expressions.constant(Periods.valueOf(period));
                    builder.or(Expressions.predicate(Ops.EQ, periodBase, value));
                }
                return builder;
            }
            Expression<Periods> value = Expressions.constant(Periods.valueOf(this.values.get(0)));
            return builder.or(Expressions.predicate(resolveOperator(), periodBase, value));
        }
        if (this.columnId.equals("isDefault")) {
            Path<Boolean> booleanBase = Expressions.path(Boolean.class, documentNumberGenerator, this.columnId);
            Expression<Boolean> value = Expressions.constant(Boolean.valueOf(this.values.get(0)));
            return builder.or(Expressions.predicate(resolveOperator(), booleanBase, value));
        }

        throw new RuntimeException("Not valid filter!");
    }
}