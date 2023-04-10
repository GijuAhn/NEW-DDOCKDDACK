package com.ddockddack.domain.report.entity;

public enum ReportType {
    DISGUSTING("혐오감을 유발하는 컨텐츠"),
    SPAM("스팸 또는 혼동을 야기하는 컨텐츠"),
    VIOLENT("폭력적인 컨텐츠"),
    SEXUAL("선정적인 컨텐츠");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
