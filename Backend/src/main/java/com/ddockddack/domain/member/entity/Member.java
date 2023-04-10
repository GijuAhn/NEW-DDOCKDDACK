package com.ddockddack.domain.member.entity;

import com.ddockddack.global.util.BaseEntity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "email" })})
@DynamicInsert
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 30, nullable = false)
    private String email;

    @Column(length = 15, nullable = false)
    private String nickname;

    @Column(length = 300, nullable = false)
    private String profile;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Role role; // member, ADMIN, BAN

    @Column(name = "member_release_date", nullable = true)
    private LocalDate releaseDate;


    @Builder
    public Member(String email, String nickname, String profile, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.profile = profile;
        this.role = role;
    }

    public void modifyNickname(String nickname) {
        this.nickname = nickname;
    }

    public void modifyProfile(String profile) {
        this.profile = profile;
    }
}
