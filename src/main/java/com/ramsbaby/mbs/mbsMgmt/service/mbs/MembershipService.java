package com.ramsbaby.mbs.mbsMgmt.service.mbs;

import com.ramsbaby.mbs.mbsMgmt.entity.MembershipDetailEntity;
import com.ramsbaby.mbs.mbsMgmt.entity.MembershipEntity;
import com.ramsbaby.mbs.mbsMgmt.entity.MembershipUserEntity;
import com.ramsbaby.mbs.mbsMgmt.model.MembershipModel;
import com.ramsbaby.mbs.mbsMgmt.model.MembershipUserModel;

import java.util.List;
import java.util.Optional;

public interface MembershipService {

    //멤버십 전체조회
    public List<MembershipModel.MembershipDetailModel> getMembershipAll(MembershipUserModel membershipUserModel);

    //멤버십 상세조회
    public MembershipModel.MembershipDetailModel getMembershipDetail(String userId, String membershipId);

    //멤버십 삭제(비활성화)
    public boolean deleteMembership(String userId, String membershipId);

    //포인트적립
    public boolean earnPoint(String userId, MembershipModel.MembershipDetailModel.EarnPointParam param);

    //멤버십 등록
    public MembershipUserModel registMembership(MembershipUserModel membershipUserModel, MembershipModel.MembershipRegistParam param);


    //(멤버십 등록) 멤버십디테일 확인 -> 없으면 멤버십디테일 생성.
    public Optional<MembershipDetailEntity> isExistMembershipDetail(String userId, String membershipId, String membershipName);

    //(멤버십 등록) 멤버십 존재하는지 확인 -> 없으면 에러 발생(일치하는 멤버십이 없습니다)
    public Optional<MembershipEntity> findMembershipIsExist(String userId, String membershipName);

    //(멤버십 등록) 유저 확인 -> 유저 없으면 유저 생성.
    public void isExistUser(String userId);

    //(공통) user entity optional로 가져오기 -> 없으면 에러 발생
    public Optional<MembershipUserEntity> findMembershipUser(String userId);

    //(공통) membership entity optional로 가져오기 -> 없으면 에러 발생
    public Optional<MembershipEntity> findMembership(String membershipId);

    //(공통) membership entity optional로 가져오기 -> 없으면 에러 발생
    public Optional<MembershipEntity> findMembership(String membershipId, String membershipName);
}