package com.data.dataxer.models.page;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.function.Function;

public interface CustomPageDTO<T> extends Page<T> {
    BigDecimal getTotalPrice();

    <U> @NotNull CustomPageDTO<U> map(Function<? super T, ? extends U> var1);
}
