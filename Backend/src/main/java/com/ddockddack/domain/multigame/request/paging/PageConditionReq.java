package com.ddockddack.domain.multigame.request.paging;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
@Schema(description = "베스트컷, 게임 목록 요청 DTO")
public class PageConditionReq {
    private static final int DEFAULT_PAGE = 0;
    private static final int PAGE_SIZE = 9;

    @Schema(description = "조회 기간(DAY, WEEK MONTH, HALF_YEAR, ALL")
    private PeriodCondition period;
    @Schema(description = "검색어 유형(MEMBER, GAME)")
    private SearchCondition search;
    @Schema(description = "검색어")
    private String keyword;
    private Pageable pageable;

    @Builder
    public PageConditionReq(String order,
                            String period,
                            String search,
                            String keyword,
                            Integer page) {
        this.period = toPeriodCond(period);
        this.search = toSearchCond(search);
        this.keyword = keyword;
        this.pageable = toPageable(page, toOrderCond(order));
    }

    private OrderCondition toOrderCond(String order){
        return (order == null || order.isBlank()) ? OrderCondition.POPULARITY : OrderCondition.valueOf(order);
    }

    private PeriodCondition toPeriodCond(String period){
        return (period == null || period.isBlank()) ? null : PeriodCondition.valueOf(period);
    }

    private SearchCondition toSearchCond(String search){
        return (search == null || search.isBlank()) ? null : SearchCondition.valueOf(search);
    }

    private Pageable toPageable(Integer pageNum, OrderCondition orderCondition){
        Integer page = DEFAULT_PAGE;
        if(pageNum!=null){
            page = pageNum-1;
        }
        return PageRequest.of(page, PAGE_SIZE, Direction.DESC, orderCondition.getSort());
    }


}
