package com.ddockddack.domain.bestcut.repository;

import com.ddockddack.domain.bestcut.entity.BestcutLike;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BestcutLikeRepository {

    private final EntityManager em;


    public Long save(BestcutLike bestcutLike) {
        em.persist(bestcutLike);
        return bestcutLike.getId();
    }

    public Optional<BestcutLike> findOne(Long bestcutId, Long memberId) {
        List<BestcutLike> resultList = em.createQuery(
                "SELECT l " +
                    "FROM BestcutLike l " +
                    "WHERE l.bestcut.id = :bestcut AND l.member.id = :member"
                , BestcutLike.class)
                .setParameter("bestcut", bestcutId)
            .setParameter("member", memberId)
            .getResultList();

        return resultList.stream().findAny();
    }

    public void delete(BestcutLike bestcutLike) {
        em.remove(bestcutLike);
    }

    public void deleteByBestcutId(@Param("id") Long bestcutId){
        em.createQuery("DELETE FROM BestcutLike l WHERE l.bestcut.id = :id").setParameter("id", bestcutId).executeUpdate();
    }

    public void deleteByBestcutIdIn(@Param("ids") List<Long> bestcutIds) {
        em.createQuery("DELETE FROM BestcutLike l WHERE l.bestcut.id in :ids").setParameter("ids", bestcutIds).executeUpdate();
    }

    public void deleteByMemberId(@Param("id") Long memberId){
        em.createQuery("DELETE FROM BestcutLike l WHERE l.member.id = :id").setParameter("id", memberId).executeUpdate();
    }
}
