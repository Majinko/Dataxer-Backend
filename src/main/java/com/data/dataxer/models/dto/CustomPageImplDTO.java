package com.data.dataxer.models.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class CustomPageImplDTO<T> extends PageImpl<T> implements Page<T> {
    private long totalPrice;

    public CustomPageImplDTO(List<T> content, Pageable pageable, long total, long totalPrice) {
        super(content, pageable, total);

        this.totalPrice = totalPrice;
    }

    public CustomPageImplDTO(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public CustomPageImplDTO(List<T> content) {
        super(content);
    }

    public long getTotalPrice() {
        return totalPrice;
    }
}


