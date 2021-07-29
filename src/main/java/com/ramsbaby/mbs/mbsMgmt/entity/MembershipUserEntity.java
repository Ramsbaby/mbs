package com.ramsbaby.mbs.mbsMgmt.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "membership_user")
@EntityListeners(AuditingEntityListener.class)

public class MembershipUserEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @Builder.Default
    private List<MembershipDetailEntity> membershipDetails = new ArrayList<>();

    public List<MembershipDetailEntity> getMembershipDetails() {
        return membershipDetails;
    }

    private MembershipUserEntity(String userId) {
        this.userId = userId;
    }

    public static MembershipUserEntity create(String userId) {
        return new MembershipUserEntity(userId);
    }
}
