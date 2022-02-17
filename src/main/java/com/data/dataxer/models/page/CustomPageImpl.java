package com.data.dataxer.models.page;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public class CustomPageImpl<T> extends PageImpl<T> implements CustomPageDTO<T> {
    private final long total;
    private final BigDecimal totalPrice;

    public CustomPageImpl(List<T> content, Pageable pageable, long total, BigDecimal totalPrice) {
        super(content, pageable, total);

        this.total = total;
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public <U> @NotNull CustomPageDTO<U> map(Function<? super T, ? extends U> converter) {
        return new CustomPageImpl(this.getConvertedContent(converter), this.getPageable(), this.total, this.totalPrice);
    }
}


