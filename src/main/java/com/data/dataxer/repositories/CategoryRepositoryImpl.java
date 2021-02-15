package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Category;
import org.springframework.stereotype.Repository;
import works.hacker.mptt.classic.MpttRepositoryImpl;

@Repository
public class CategoryRepositoryImpl extends MpttRepositoryImpl<Category> implements CategoryRepositoryCustom {
}
