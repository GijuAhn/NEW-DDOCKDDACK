package com.ddockddack.domain.ranking.service;

import com.ddockddack.domain.member.entity.Member;
import com.ddockddack.domain.member.repository.MemberRepository;
import com.ddockddack.domain.ranking.entity.Ranking;
import com.ddockddack.domain.ranking.repository.RankingRepository;
import com.ddockddack.domain.ranking.request.RankingSaveReq;
import com.ddockddack.domain.ranking.response.RankingRes;
import com.ddockddack.domain.report.entity.ReportType;
import com.ddockddack.domain.report.entity.ReportedRanking;
import com.ddockddack.domain.report.repository.ReportedRankingRepository;
import com.ddockddack.domain.singlegame.entity.SingleGame;
import com.ddockddack.domain.singlegame.repository.SingleGameRepository;
import com.ddockddack.global.aws.AwsS3;
import com.ddockddack.global.error.ErrorCode;
import com.ddockddack.global.error.exception.AlreadyExistResourceException;
import com.ddockddack.global.error.exception.NotFoundException;
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

    private final ReportedRankingRepository reportedRankingRepository;
    private final RankingRepository rankingRepository;
    private final SingleGameRepository singleGameRepository;
    private final MemberRepository memberRepository;
    private final AwsS3 awsS3;

    /**
     * 랭킹 전체조회
     *
     * @param gameId
     * @return
     */
    public List<RankingRes> getRanking(Long gameId) {
        return rankingRepository.findByGameId(gameId).stream()
            .map(RankingRes::from)
            .collect(Collectors.toList());
    }

    /**
     * 랭킹 등록
     *
     * @param rankingSaveReq
     * @param memberId
     */
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


    /**
     * 랭킹 신고
     * @param memberId
     * @param rankingId
     * @param reportType
     */
    @Transactional
    public void reportRanking(Long memberId, Long rankingId, ReportType reportType) {

        // 이미 신고했는지 검증
        final boolean exist = reportedRankingRepository.exist(memberId, rankingId);

        if (exist) {
            throw new AlreadyExistResourceException(ErrorCode.ALREADY_EXIST_REPORTEDGAME);
        }

        final Ranking ranking = checkRankingValidation(rankingId);
        final Member reportMember = memberRepository.getReferenceById(memberId);

        final ReportedRanking reportedRanking = ReportedRanking.builder()
            .reportMember(reportMember)
            .ranking(ranking)
            .reportType(reportType)
            .build();

        reportedRankingRepository.save(reportedRanking);
    }


    private Ranking checkRankingValidation(Long rankingId) {
        return rankingRepository.findById(rankingId).orElseThrow(() ->
            new NotFoundException(ErrorCode.GAME_NOT_FOUND));
    }


    /**
     * 랭킹 점수 비교
     *
     * @param rankingSaveReq
     * @param findRanking
     * @return
     */
    private boolean compareScore(RankingSaveReq rankingSaveReq, Optional<Ranking> findRanking) {
        return rankingSaveReq.getScore() > findRanking.get().getScore();
    }

}
