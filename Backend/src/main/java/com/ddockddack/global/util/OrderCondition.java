package com.ddockddack.global.util;

public enum OrderCondition {
    RECENT("createdDate"),
    POPULARITY("popularity")
    ;

    private final String sort;

    OrderCondition(String sort) {
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }
}
