package com.ramsbaby.mbs.mbsMgmt.repository;

import com.ramsbaby.mbs.mbsMgmt.entity.MembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipRepository extends JpaRepository<MembershipEntity, String> {
    Optional<MembershipEntity> findByMembershipIdAndMembershipName(String userId, String membershipId);
}
