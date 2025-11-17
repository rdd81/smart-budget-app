package com.smartbudget.repository.projection;

public interface MetricsTotalsProjection {
    Long getTotal();
    Long getAccepted();
    Long getRejected();
}

