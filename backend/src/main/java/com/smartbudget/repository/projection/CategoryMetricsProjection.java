package com.smartbudget.repository.projection;

import java.util.UUID;

public interface CategoryMetricsProjection {
    UUID getCategoryId();
    String getCategoryName();
    Long getTotal();
    Long getAccepted();
    Long getRejected();
}

