package com.ramsbaby.mbs.mbsMgmt.service.mbs;

import com.ramsbaby.mbs.advice.exception.*;
import com.ramsbaby.mbs.mbsMgmt.entity.MembershipDetailEntity;
import com.ramsbaby.mbs.mbsMgmt.entity.MembershipEntity;
import com.ramsbaby.mbs.mbsMgmt.entity.MembershipUserEntity;
import com.ramsbaby.mbs.mbsMgmt.model.MembershipModel;
import com.ramsbaby.mbs.mbsMgmt.model.MembershipUserModel;
import com.ramsbaby.mbs.mbsMgmt.repository.MembershipDetailRepository;
import com.ramsbaby.mbs.mbsMgmt.repository.MembershipRepository;
import com.ramsbaby.mbs.mbsMgmt.repository.MembershipUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    private final MembershipDetailRepository membershipDetailRepository;
    private final MembershipUserRepository membershipUserRepository;
    private final MembershipRepository membershipRepository;

    @PersistenceContext    // EntityManagerFactory가 DI 할 수 있도록 어노테이션 설정
    private EntityManager entityManager;

    //멤버십 전체조회
    //user entity -> membershipDetails
    public List<MembershipModel.MembershipDetailModel> getMembershipAll(MembershipUserModel membershipUserModel) {
        MembershipUserEntity membershipUserInfo = membershipUserRepository.
                findById(membershipUserModel.getUserId()).
                orElseThrow(CMembershipDetailNotExistException::new);
        return MembershipModel.setEntity(membershipUserInfo).getMembershipDetailModels();
    }

    //멤버십 상세조회
    public MembershipModel.MembershipDetailModel getMembershipDetail(String userId, String membershipId) {
        MembershipDetailEntity membershipDetailEntity = membershipDetailRepository.
                findByUserAndMembershipId(findMembershipUser(userId), findMembership(membershipId)).
                orElseThrow(CMembershipDetailNotExistException::new);
        return MembershipModel.MembershipDetailModel.setEntity(membershipDetailEntity);
    }

    //멤버십 삭제(비활성화)
    public boolean deleteMembership(String userId, String membershipId) {
        Optional<MembershipDetailEntity> membershipDetailEntityOptional = Optional.of(membershipDetailRepository.
                findByUserAndMembershipId(findMembershipUser(userId), findMembership(membershipId)).
                orElseThrow(CMembershipDetailNotExistException::new));

        if (membershipDetailEntityOptional.isPresent() == true) {// 값이 있으면 상태값을 N으로 수정(비활성화)
            membershipDetailEntityOptional.get().setMembershipStatus("N");
            membershipDetailRepository.save(membershipDetailEntityOptional.get());
        }

        return true;
    }

    //포인트적립
    public boolean earnPoint(String userId, MembershipModel.MembershipDetailModel.EarnPointParam param) {
        Optional<MembershipDetailEntity> membershipDetailEntityOptional = Optional.of(membershipDetailRepository.
                findByUserAndMembershipId(findMembershipUser(userId), findMembership(param.getMembershipId())).
                orElseThrow(CMembershipDetailNotExistException::new));

        if (membershipDetailEntityOptional.isPresent() == true &&
                membershipDetailEntityOptional.get().getMembershipStatus().equals("Y")) {// 디테일데이터가 존재하면 포인트 적립, 활성화된 유저는 적립가능.
            Double earnedPoint = param.getAmount() * 0.01;//결제값의 1% 적립
            Long point = membershipDetailEntityOptional.get().getPoint();//기존포인트
            membershipDetailEntityOptional.get().setPoint(point + (long) Math.floor(earnedPoint.longValue()));
            membershipDetailRepository.save(membershipDetailEntityOptional.get());
        } else if (membershipDetailEntityOptional.isPresent() == true &&
                membershipDetailEntityOptional.get().getMembershipStatus().equals("N")) {// 디테일데이터가 존재하면 포인트 적립, 비활성화된 유저는 적립불가.
            throw new CMembershipStatusDisabledException();
        }

        return true;
    }

    //멤버십 등록
    public MembershipUserModel registMembership(MembershipUserModel membershipUserModel, MembershipModel.MembershipRegistParam param) {
        //유저 존재 확인 -> 없으면 생성
        isExistUser(membershipUserModel.getUserId());

        //멤버십 디테일 엔티티 생성
        MembershipDetailEntity membershipDetailEntity = MembershipDetailEntity.
                create(findMembership(param.getMembershipId()).get(),
                        findMembershipUser(membershipUserModel.getUserId()).get(),
                        param.getPoint(), "Y");

        //해당 유저의 멤버십디테일이 있는지 조회 -> 없으면 멤버십디테일 생성
        //멤버십 디테일 DB에 저장 후 바로 업데이트(전체조회를 위해서)
        Optional<MembershipDetailEntity> isExistDetail = isExistMembershipDetail(membershipUserModel.getUserId(), param.getMembershipId(), param.getMembershipName());
        if (isExistDetail.isPresent() == true && isExistDetail.get().getMembershipStatus().equals("Y")) {
            //등록과정에서 멤버십디테일이 있고, 활성화된 멤버십 디테일이면 -> 멤버십 중복 에러 발생시킴
            throw new CMembershipDetailIsExistException();
        } else if (isExistDetail.isPresent() == true && isExistDetail.get().getMembershipStatus().equals("N")) {
            //등록과정에서 멤버십디테일이 있고, 비활성화된 멤버십 디테일이면 -> 멤버십 중복 에러 발생시킴
            isExistDetail.get().setMembershipStatus("Y");
            membershipDetailRepository.saveAndFlush(isExistDetail.get());
        } else {//멤버십디테일이 없으면 생성
            membershipDetailRepository.saveAndFlush(membershipDetailEntity);
        }

        //영속성 컨텍스트 초기화
        entityManager.clear();
        entityManager.close();

        return membershipUserModel;
    }


    //(멤버십 등록) 멤버십디테일 확인 -> 없으면 멤버십디테일 생성.
    public Optional<MembershipDetailEntity> isExistMembershipDetail(String userId, String membershipId, String membershipName) {
        return Optional.ofNullable(membershipDetailRepository.
                findByUserAndMembershipId(findMembershipUser(userId), findMembershipIsExist(membershipId, membershipName))).get();
    }

    //(멤버십 등록) 멤버십 존재하는지 확인 -> 없으면 에러 발생(일치하는 멤버십이 없습니다)
    public Optional<MembershipEntity> findMembershipIsExist(String userId, String membershipName) {
        return Optional.of(membershipRepository.findByMembershipIdAndMembershipName(userId, membershipName).
                orElseThrow(CMembershipNotExistException::new));
    }

    //(멤버십 등록) 유저 확인 -> 유저 없으면 유저 생성.
    public void isExistUser(String userId) {
        //유저 엔티티 생성
        MembershipUserEntity membershipUserEntity = MembershipUserEntity.create(userId);
        membershipUserRepository.findById(userId).orElseGet(() -> membershipUserRepository.save(membershipUserEntity));
    }

    //(공통) user entity optional로 가져오기 -> 없으면 에러 발생
    public Optional<MembershipUserEntity> findMembershipUser(String userId) {
        return Optional.of(membershipUserRepository.findById(userId).orElseThrow(CMembershipUserNotExistException::new));
    }

    //(공통) membership entity optional로 가져오기 -> 없으면 에러 발생
    public Optional<MembershipEntity> findMembership(String membershipId) {
        return Optional.of(membershipRepository.findById(membershipId).orElseThrow(CMembershipNotExistException::new));
    }

    //(공통) membership entity optional로 가져오기 -> 없으면 에러 발생
    public Optional<MembershipEntity> findMembership(String membershipId, String membershipName) {
        return Optional.of(membershipRepository.findByMembershipIdAndMembershipName(membershipId, membershipName).orElseThrow(CMembershipNotExistException::new));
    }
}