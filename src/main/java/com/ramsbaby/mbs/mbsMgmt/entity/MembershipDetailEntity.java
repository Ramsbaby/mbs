package com.ramsbaby.mbs.mbsMgmt.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "membership_detail")
@SequenceGenerator(name = "MEMBERSHIP_DETAIL_SEQ_GENERATOR", sequenceName = "SEQ", initialValue = 1, allocationSize = 1)
@EntityListeners(AuditingEntityListener.class)
public class MembershipDetailEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE, //사용할 전략을 시퀀스로  선택
            generator = "MEMBERSHIP_DETAIL_SEQ_GENERATOR" //식별자 생성기를 설정해놓은  USER_SEQ_GEN으로 설정
    )
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    private MembershipEntity membershipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private MembershipUserEntity user;

    @Column(name = "membership_status", length = 1, columnDefinition = "varchar(1)")
    @ColumnDefault("'Y'")
    private String membershipStatus;

    @CreatedDate
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "point")
    private Long point;

    //멤버십 상태가 없는 생성자
    private MembershipDetailEntity(MembershipEntity membershipId, MembershipUserEntity user, Long point) {
        this.membershipId = membershipId;
        this.user = user;
        this.point = point;
    }

    //멤버십 상태가 있는 생성자
    private MembershipDetailEntity(MembershipEntity membershipId, MembershipUserEntity user, Long point, String membershipStatus) {
        this.membershipId = membershipId;
        this.user = user;
        this.point = point;
        this.membershipStatus = membershipStatus;
    }

    public static MembershipDetailEntity create(MembershipEntity membershipId, MembershipUserEntity user, Long point) {
        return new MembershipDetailEntity(membershipId, user, point);
    }

    public static MembershipDetailEntity create(MembershipEntity membershipId, MembershipUserEntity user, Long point, String membershipStatus) {
        return new MembershipDetailEntity(membershipId, user, point, membershipStatus);
    }
}
