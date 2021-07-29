package com.ramsbaby.mbs.mbsMgmt.contoller;

import com.ramsbaby.mbs.mbsMgmt.entity.MembershipDetailEntity;
import com.ramsbaby.mbs.mbsMgmt.entity.MembershipUserEntity;
import com.ramsbaby.mbs.mbsMgmt.repository.MembershipDetailRepository;
import com.ramsbaby.mbs.mbsMgmt.repository.MembershipRepository;
import com.ramsbaby.mbs.mbsMgmt.repository.MembershipUserRepository;
import com.ramsbaby.mbs.mbsMgmt.service.mbs.MembershipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MbsDetailSearchTest {
    private static final String X_USER_ID = "X-USER-ID";
    private static final String MEMBERSHIP_ID1 = "shinsegae";
    private static final String MEMBERSHIP_ID2 = "spc";
    private static final String MEMBERSHIP_ID3 = "cj";
    private static final String MEMBERSHIP_NAME1 = "shinsegaepoint";
    private static final String MEMBERSHIP_NAME2 = "happypoint";
    private static final String MEMBERSHIP_NAME3 = "cjone";
    private static final String MEMBERSHIP_STATUS = "Y";
    private static final Long POINT = 5000L;

    private static final String USER_ID = "test1";
    private static final String USER_ID2 = "test2";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MembershipDetailRepository membershipDetailRepository;
    @Autowired
    private MembershipUserRepository membershipUserRepository;
    @Autowired
    private MembershipRepository membershipRepository;
    @InjectMocks
    private MembershipServiceImpl mbsService;
    @PersistenceContext    // EntityManagerFactory가 DI 할 수 있도록 어노테이션 설정
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() throws Exception {
        //유저, 멤버십디테일 생성
        MembershipUserEntity membershipUserEntity = MembershipUserEntity.create(USER_ID);
        membershipUserRepository.saveAndFlush(membershipUserEntity.builder().userId(USER_ID).build());

        //영속성 컨텍스트 초기화
        entityManager.clear();
        entityManager.close();

        //멤버십 디테일 생성 (멤버십상태 : 활성화) - MEMBERSHIP_ID1
        MembershipDetailEntity membershipDetailEntity = MembershipDetailEntity.
                create(membershipRepository.findById(MEMBERSHIP_ID1).get(),
                        membershipUserRepository.findById(USER_ID).get(),
                        POINT, "Y");
        membershipDetailRepository.saveAndFlush(membershipDetailEntity);


        //멤버십 디테일 생성 (멤버십상태 : 활성화) - MEMBERSHIP_ID2
        MembershipDetailEntity membershipDetailEntity2 = MembershipDetailEntity.
                create(membershipRepository.findById(MEMBERSHIP_ID2).get(),
                        membershipUserRepository.findById(USER_ID).get(),
                        POINT, "Y");
        membershipDetailRepository.saveAndFlush(membershipDetailEntity2);
    }

    @Test
    @DisplayName("멤버십상세조회 - 정상")
    void 멤버십상세조회_정상() throws Exception {
        // given
        // 'test1'인 유저는 MEMBERSHIP_ID1('shinsegae')를 이미 가지고 있는 상황

        // when
        // 'test1'인 유저의 MEMBERSHIP_ID1('shinsegae')를 상세조회 하는 경우
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/membership/" + MEMBERSHIP_ID1)
                .header("Content-Type", "application/json")
                .header(X_USER_ID, USER_ID))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @DisplayName("멤버십상세조회 - 상세조회할 멤버십이 없는 경우 - 에러")
    void 멤버십상세조회_상세조회할멤버십이없는_Exception() throws Exception {
        // given
        // 'test1'인 유저는 MEMBERSHIP_ID1('shinsegae')를 이미 가지고 있는 상황

        // when
        // 'test1'인 유저는 MEMBERSHIP_ID2('cj')를 조회하려는 경우 - 에러
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/membership/" + MEMBERSHIP_ID3)
                .header("Content-Type", "application/json")
                .header(X_USER_ID, USER_ID))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.response").isEmpty())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status").value(404));
    }

    @Test
    @DisplayName("멤버십상세조회 - 조회대상인 유저가 없는 경우 - 에러")
    void 멤버십상세조회_조회대상인유저가없는_Exception() throws Exception {
        // given
        // 'test1'인 유저는 MEMBERSHIP_ID1('shinsegae')를 이미 가지고 있는 상황

        // when
        // 'test2'인 유저의 MEMBERSHIP_ID1('shinsegae')를 조회하려는 경우 - 에러
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/membership/" + MEMBERSHIP_ID1)
                .header("Content-Type", "application/json")
                .header(X_USER_ID, USER_ID2))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.response").isEmpty())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status").value(404));
    }


}
