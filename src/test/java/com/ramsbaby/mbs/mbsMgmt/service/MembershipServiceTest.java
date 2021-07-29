package com.ramsbaby.mbs.mbsMgmt.service;


import com.ramsbaby.mbs.mbsMgmt.entity.MembershipDetailEntity;
import com.ramsbaby.mbs.mbsMgmt.entity.MembershipEntity;
import com.ramsbaby.mbs.mbsMgmt.entity.MembershipUserEntity;
import com.ramsbaby.mbs.mbsMgmt.model.MembershipModel;
import com.ramsbaby.mbs.mbsMgmt.model.MembershipUserModel;
import com.ramsbaby.mbs.mbsMgmt.repository.MembershipDetailRepository;
import com.ramsbaby.mbs.mbsMgmt.repository.MembershipRepository;
import com.ramsbaby.mbs.mbsMgmt.repository.MembershipUserRepository;
import com.ramsbaby.mbs.mbsMgmt.service.mbs.MembershipServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MembershipServiceTest {
    private static final String USER_ID1 = "test1";
    private static final String USER_ID2 = "test2";

    private static final String MEMBERSHIP_ID1 = "spc";
    private static final String MEMBERSHIP_ID2 = "shinsegae";
    private static final String MEMBERSHIP_ID3 = "cj";
    private static final String MEMBERSHIP_ID4 = "anyId";

    private static final String MEMBERSHIP_NAME1 = "happypoint";
    private static final String MEMBERSHIP_NAME2 = "shinsegaepoint";
    private static final String MEMBERSHIP_NAME3 = "cjone";
    private static final String MEMBERSHIP_NAME4 = "anyName";

    @Autowired
    private MembershipUserRepository membershipUserRepository;

    @Autowired
    private MembershipDetailRepository membershipDetailRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipServiceImpl service;

    @Autowired
    MessageSource messageSource;

    @PersistenceContext    // EntityManagerFactory가 DI 할 수 있도록 어노테이션 설정
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() throws Exception {
        //유저, 멤버십디테일 생성
        MembershipUserEntity membershipUserEntity = MembershipUserEntity.create(USER_ID1);
        membershipUserRepository.saveAndFlush(membershipUserEntity.builder().userId(USER_ID1).build());

        //영속성 컨텍스트 초기화
        entityManager.clear();
        entityManager.close();

        //멤버십 디테일 생성 (멤버십상태 : 활성화) - MEMBERSHIP_ID1
        MembershipDetailEntity membershipDetailEntity = MembershipDetailEntity.
                create(membershipRepository.findById(MEMBERSHIP_ID1).get(),
                        membershipUserRepository.findById(USER_ID1).get(),
                        1000L, "Y");
        membershipDetailRepository.saveAndFlush(membershipDetailEntity);
    }

    @Test
    @DisplayName("유저 조회 성공")
    void 유저_조회_성공() {
        // given
        MembershipUserEntity userEntity = MembershipUserEntity.builder().userId(USER_ID1).build();

        //when
        membershipUserRepository.save(userEntity);
        MembershipUserEntity userEntity2 = service.findMembershipUser(USER_ID1).get();

        //then
        Assertions.assertNotNull(userEntity2);
    }

    @Test
    @DisplayName("유저 없으면 에러")
    void 유저_없으면_에러() {
        // given

        //when
        Optional<MembershipUserEntity> membershipUserInfo = Optional.
                ofNullable(membershipUserRepository.findById(USER_ID2)).get();

        //then
        assertThat(membershipUserInfo.isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("멤버십 정보 조회 - 성공")
    void 멤버십_정보_조회_성공() {
        //given - when
        MembershipEntity membershipEntity1 = service.findMembership(MEMBERSHIP_ID1).get();
        //then
        Assertions.assertNotNull(membershipEntity1);
    }

    @Test
    @DisplayName("멤버십 정보 조회2 - by MEMBERSHIP_ID, MEMBERSHIP_NAME - 성공")
    void 멤버십_정보_조회_BY_ID_NAME_성공() {
        //given - when
        MembershipEntity membershipEntity = service.findMembership(MEMBERSHIP_ID1, MEMBERSHIP_NAME1).get();
        //then
        Assertions.assertNotNull(membershipEntity);
    }

    @Test
    @DisplayName("멤버십 정보 조회 에러")
    void 멤버십_정보_조회_에러() {
        //given

        //when
        Optional<MembershipEntity> membershipInfo = Optional.
                ofNullable(membershipRepository.findById(MEMBERSHIP_ID4)).get();

        //then
        assertThat(membershipInfo.isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("멤버십 정보 조회 by MEMBERSHIP_ID, MEMBERSHIP_NAME - 에러")
    void 멤버십_정보_조회_BY_ID_NAME_에러() {
        //given

        //when - 없는 유저를 찾는 경우
        Optional<MembershipEntity> membershipInfo = Optional.
                ofNullable(membershipRepository.findByMembershipIdAndMembershipName(MEMBERSHIP_ID4, MEMBERSHIP_NAME4)).get();

        //then
        assertThat(membershipInfo.isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("멤버십 전체 조회 - 성공")
    void 멤버십_전체_조회_성공() {
        //given

        //when
        MembershipUserModel membershipUserModel = MembershipUserModel.of(USER_ID1);
        List resultList = service.getMembershipAll(membershipUserModel);
        //then
        Assertions.assertNotNull(resultList);
    }

    @Test
    @DisplayName("1. 멤버십 전체 조회하기 - 에러")
    void 멤버십_전체_조회_에러() {
        //given

        //when
        //USER2 대입한 경우
        MembershipUserModel membershipUserModel = MembershipUserModel.of(USER_ID2);

        Optional<MembershipUserEntity> membershipUserInfo = Optional.
                ofNullable(membershipUserRepository.findById(membershipUserModel.getUserId())).get();

        //then
        assertThat(membershipUserInfo.isPresent()).isEqualTo(false);

    }

    @Test
    @DisplayName("3. 멤버십 삭제(해지)하기 - 성공")
    void 멤버십_삭제하기_성공() {
        //given

        //when
        Boolean result = service.deleteMembership(USER_ID1, MEMBERSHIP_ID1);
        //then
        assertThat(true).isEqualTo(result);
    }

    @Test
    @DisplayName("3. 멤버십 삭제(해지)하기 - 에러")
    void 멤버십_삭제하기_에러() {
        //given

        //when
        //등록되지 않은 유저 조회
        Optional<MembershipEntity> membershipEntity =
                Optional.ofNullable(membershipRepository.findByMembershipIdAndMembershipName(USER_ID2, MEMBERSHIP_ID1)).get();

        //then
        assertThat(membershipEntity.isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("4. 멤버십 상세 조회 - 성공")
    void 멤버십_상세조회_성공() {
        //given

        //when - 등록된 유저 조회
        MembershipModel.MembershipDetailModel result = service.getMembershipDetail(USER_ID1, MEMBERSHIP_ID1);

        //then
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("4. 멤버십 상세 조회 - 멤버십ID - 에러")
    void 멤버십_상세조회_멤버십ID_에러() {
        //given

        //when
        Optional<MembershipEntity> membershipEntity =
                Optional.ofNullable(membershipRepository.findByMembershipIdAndMembershipName(USER_ID1, MEMBERSHIP_ID2)).get();

        //then
        assertThat(membershipEntity.isPresent()).isEqualTo(false);
    }

    @Test
    @DisplayName("5. 포인트 적립 - 성공")
    void 멤버십_포인트_적립_성공() {
        //given
        MembershipModel.MembershipDetailModel.EarnPointParam param =
                MembershipModel.MembershipDetailModel.EarnPointParam.of(MEMBERSHIP_ID1, MEMBERSHIP_NAME1, 100L);
        //when
        Boolean result = service.earnPoint(USER_ID1, param);

        //then
        assertThat(true).isEqualTo(result);
    }

    @Test
    @DisplayName("5. 포인트 적립 에러 - 멤버십 디테일이 존재하지 않을 경우 - 에러")
    void 멤버십_포인트_적립_멤버십디테일없음_에러() {
        //given
        //MEMBERSHIP_ID3, MEMBERSHIP_NAME3을 파라미터에 대입.
        MembershipModel.MembershipDetailModel.EarnPointParam param =
                MembershipModel.MembershipDetailModel.EarnPointParam.of(MEMBERSHIP_ID3, MEMBERSHIP_NAME3, 100L);

        //when
        Optional<MembershipDetailEntity> membershipDetailInfo = Optional.ofNullable(membershipDetailRepository.
                findByUserAndMembershipId(membershipUserRepository.findById(USER_ID1), membershipRepository.findById(param.getMembershipId()))).get();

        //then
        assertThat(membershipDetailInfo.isPresent()).isEqualTo(false);
    }

    // code정보에 해당하는 메시지를 조회합니다.
    private String getMessage(String code) {
        return getMessage(code, null);
    }

    // code정보, 추가 argument로 현재 locale에 맞는 메시지를 조회합니다.
    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
