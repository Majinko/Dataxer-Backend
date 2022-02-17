package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Category;

import java.util.List;

public interface QCategoryRepository  {
    List<Category> allUserCategoryByTime(String uid, Long appProfileId);
}
