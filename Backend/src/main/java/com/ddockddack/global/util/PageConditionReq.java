package com.ddockddack.global.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "베스트컷, 게임 목록 요청 DTO")
public class PageConditionReq {

    @Schema(description = "최신순(RECENT), 인기순(POPULARITY")
    private String order;
    @Schema(description = "조회 기간(DAY, WEEK MONTH, HALF_YEAR, ALL")
    private String period;

    @Schema(description = "검색어 유형(MEMBER, GAME)")
    private String search;
    @Schema(description = "검색어")
    private String keyword;
    @Schema(description = "요청 페이지")
    private Integer page = 1;

    public PageCondition toEntity() {
        return PageCondition.builder()
                .order(this.order)
                .period(this.period)
                .search(this.search)
                .keyword(this.keyword)
                .page(this.page-1)
                .build();
    }

    @Override
    public String toString() {
        return "PageConditionReq{" +
                "order='" + order + '\'' +
                ", period='" + period + '\'' +
                ", search='" + search + '\'' +
                ", keyword='" + keyword + '\'' +
                ", page=" + page +
                '}';
    }
}
