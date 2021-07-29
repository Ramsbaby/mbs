package com.ramsbaby.mbs.mbsMgmt.repository;

import com.ramsbaby.mbs.mbsMgmt.entity.MembershipDetailEntity;
import com.ramsbaby.mbs.mbsMgmt.entity.MembershipEntity;
import com.ramsbaby.mbs.mbsMgmt.entity.MembershipUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipDetailRepository extends JpaRepository<MembershipDetailEntity, String> {
    Optional<MembershipDetailEntity> findByUserAndMembershipId(Optional<MembershipUserEntity> user, Optional<MembershipEntity> membershipId);
}
