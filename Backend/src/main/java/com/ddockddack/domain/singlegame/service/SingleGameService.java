package com.ddockddack.domain.singlegame.service;

import com.ddockddack.domain.multigame.request.paging.PageConditionReq;
import com.ddockddack.domain.singlegame.entity.SingleGame;
import com.ddockddack.domain.singlegame.repository.SingleGameRepository;
import com.ddockddack.domain.singlegame.request.FaceSimilarityReq;
import com.ddockddack.domain.singlegame.response.SingleGameRes;
import com.ddockddack.global.aws.AwsS3;
import com.ddockddack.global.aws.CompareFaces;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SingleGameService {
    private final SingleGameRepository singleGameRepository;
    private final AwsS3 awsS3;
    private final CompareFaces compareFaces;

    /**
     * 싱글 게임 목록 조회
     * @return
     */
    public PageImpl<SingleGameRes> getSingleGameList(PageConditionReq pageConditionReq){
        PageImpl<SingleGame> singleGames = singleGameRepository.findSingleGames(pageConditionReq);
        List<SingleGameRes> gameList = singleGames
                .stream()
                .map(SingleGameRes::from)
                .collect(Collectors.toList());

        return new PageImpl<>(gameList, singleGames.getPageable(), singleGames.getTotalElements());
    }

    /**
     * 얼굴 유사도 비교
     *
     * @param faceSimilarityReq
     * @return
     */
    public Float getScore(FaceSimilarityReq faceSimilarityReq) throws IOException {
        byte[] target = awsS3.getObject(faceSimilarityReq.getTarget());
        byte[] source = faceSimilarityReq.getSource().getBytes();

        return compareFaces.compare(target, source);
    }
}
