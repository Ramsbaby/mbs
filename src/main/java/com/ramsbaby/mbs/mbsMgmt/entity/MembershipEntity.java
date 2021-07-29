package com.ramsbaby.mbs.mbsMgmt.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "membership")
public class MembershipEntity {
    @Id
    private String membershipId;

    private String membershipName;

    @OneToMany
    @JoinColumn(name = "membership_id")
    private List<MembershipDetailEntity> membershipDetails;
}
