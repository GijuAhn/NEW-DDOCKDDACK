package com.ddockddack.global.util;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

@Getter
@NoArgsConstructor
public class PageCondition {

    private int PAGE_SIZE = 9;
    private PeriodCondition periodCondition;
    private SearchCondition searchCondition;
    private String keyword;
    private Pageable pageable;

    @Builder
    public PageCondition(String order, String period, String search, String keyword, int page) {
        OrderCondition orderCondition = (order == null || order.isBlank()) ? OrderCondition.POPULARITY : OrderCondition.valueOf(order);
        this.periodCondition = (period == null || period.isBlank()) ? null : PeriodCondition.valueOf(period);
        this.searchCondition = (search == null || search.isBlank()) ? null : SearchCondition.valueOf(search);
        this.keyword = keyword;
        this.pageable = PageRequest.of(page, PAGE_SIZE, Direction.DESC, orderCondition.getSort());
    }

}
