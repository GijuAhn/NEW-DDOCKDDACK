package com.ddockddack.domain.ranking.service;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.ranking.entity.Ranking;
import com.ddockddack.domain.ranking.repository.RankingRepository;
import com.ddockddack.domain.ranking.request.RankingSaveReq;
import com.ddockddack.domain.ranking.response.RankingRes;
import com.ddockddack.domain.singlegame.entity.SingleGame;
import com.ddockddack.domain.singlegame.repository.SingleGameRepository;
import com.ddockddack.global.aws.AwsS3;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final SingleGameRepository singleGameRepository;
    private final MemberRepository memberRepository;
    private final AwsS3 awsS3;

    public List<RankingRes> getRanking(Long gameId) {
        return rankingRepository.findByGameId(gameId).stream()
                .map(RankingRes::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveRanking(RankingSaveReq rankingSaveReq, Long memberId) {
        String imageUrl = awsS3.multipartFileUpload(rankingSaveReq.getImage());

        Optional<Ranking> findRanking = rankingRepository.findBySingleGameIdAndMemberId(
                rankingSaveReq.getGameId(), memberId);

        if (findRanking.isPresent()) {
            if (compareScore(rankingSaveReq, findRanking)) {
                findRanking.get().changeScoreAndImage(rankingSaveReq.getScore(), imageUrl);
            }
            return;
        }

        SingleGame singleGame = singleGameRepository.getReferenceById(rankingSaveReq.getGameId());
        Member member = memberRepository.getReferenceById(memberId);
        Ranking ranking = rankingSaveReq.toEntity(singleGame, imageUrl, member);

        rankingRepository.save(ranking);
    }

    private boolean compareScore(RankingSaveReq rankingSaveReq, Optional<Ranking> findRanking) {
        return rankingSaveReq.getScore() > findRanking.get().getScore();
    }

}
