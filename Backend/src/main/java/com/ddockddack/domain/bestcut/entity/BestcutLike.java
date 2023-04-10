package com.ddockddack.domain.bestcut.entity;

import com.ddockddack.domain.member.entity.Member;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BestcutLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bestcut_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bestcut_id", foreignKey = @ForeignKey(name = "fk_bestcut_like_bestcut_id_idx"))
    private Bestcut bestcut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_bestcut_like_member_id_idx"))
    private Member member;

    @Builder
    public BestcutLike(Bestcut bestcut, Member member) {
        this.bestcut = bestcut;
        this.member = member;
    }
}
