package com.smartbudget.repository.projection;

import com.smartbudget.entity.Category;

public interface FeedbackCategoryCount {
    Category getCategory();

    Long getCorrectionCount();
}

