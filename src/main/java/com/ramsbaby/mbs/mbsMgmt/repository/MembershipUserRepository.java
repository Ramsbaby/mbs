package com.ramsbaby.mbs.mbsMgmt.repository;

import com.ramsbaby.mbs.mbsMgmt.entity.MembershipUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipUserRepository extends JpaRepository<MembershipUserEntity, String> {
    Optional<MembershipUserEntity> findByUserId(String userId);
}