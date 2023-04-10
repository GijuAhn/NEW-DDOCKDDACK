package com.ddockddack.global.util;

public enum PeriodCondition {
    DAY(1),
    WEEK(7),
    MONTH(30),
    HALF_YEAR(180),
    ALL(987654321);

    private final int period;

    PeriodCondition(int period) {
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }
}