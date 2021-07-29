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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MbsRegistTest {
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

        //멤버십 디테일 생성 (멤버십상태 : 활성화)
        MembershipDetailEntity membershipDetailEntity = MembershipDetailEntity.
                create(membershipRepository.findById(MEMBERSHIP_ID1).get(),
                        membershipUserRepository.findById(USER_ID).get(),
                        POINT, "Y");
        membershipDetailRepository.saveAndFlush(membershipDetailEntity);
    }

    @Test
    @DisplayName("멤버십 등록 - 정상")
    void 멤버십등록_정상() throws Exception {
        // given
        // 'test1'인 유저는 MEMBERSHIP_ID1('shinsegae')를 이미 가지고 있는 상황

        // when
        // 'test1'인 유저가 MEMBERSHIP_ID2('spc')를 새로 등록하는 경우 - 정상
        ResultActions resultActions =
                mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/membership")
                        .header("Content-Type", "application/json")
                        .header(X_USER_ID, USER_ID)
                        .content("{\"membershipId\":\"spc\",\"membershipName\":\"happypoint\",\"amount\":5000}"))
                        .andDo(print());
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("멤버십 등록 - 멤버십ID와 멤버십이름 불일치 - 에러")
    void 멤버십등록_멤버십ID와멤버십이름불일치_Exception() throws Exception {
        // given
        // 'test1'인 유저는 MEMBERSHIP_ID1('shinsegae')를 이미 가지고 있는 상황

        // when
        // 'test1'인 유저가 MEMBERSHIP_ID3('cj'), MEMBERSHIP_NAME('notMatche')를 등록하는 경우 - 에러
        ResultActions resultActions =
                mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/membership")
                        .header("Content-Type", "application/json")
                        .header(X_USER_ID, USER_ID)
                        .content("{\"membershipId\":\"cj\",\"membershipName\":\"notMatche\",\"amount\":5000}"))
                        .andDo(print());
        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.response").isEmpty())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status").value(404));
    }

    @Test
    @DisplayName("멤버십 등록 - 동일한멤버십이존재 - 에러")
    void 멤버십등록_동일한멤버십이존재하는_Exception() throws Exception {
        // given
        // 'test1'인 유저는 MEMBERSHIP_ID1('shinsegae')를 이미 가지고 있는 상황

        // when
        // 'test1'인 유저가 MEMBERSHIP_ID1('shinsegae')를 다시 등록하는 경우 - 에러
        ResultActions resultActions =
                mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/membership")
                        .header("Content-Type", "application/json")
                        .header(X_USER_ID, USER_ID)
                        .content("{\"membershipId\":\"shinsegae\",\"membershipName\":\"shinsegaepoint\",\"amount\":5000}"))
                        .andDo(print());
        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.response").isEmpty())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status").value(400));
    }


    @Test
    @DisplayName("멤버십 등록 - 해당 멤버십은 존재하지 않음 - 에러")
    void 멤버십등록_해당멤버십은존재하지않는_Exception() throws Exception {
        // given
        // 'test1'인 유저는 MEMBERSHIP_ID1('shinsegae')를 이미 가지고 있는 상황

        // when
        // 'test1'인 유저가 멤버십아이디('notExistMembership')를 등록하는 경우 - 에러
        ResultActions resultActions =
                mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/membership")
                        .header("Content-Type", "application/json")
                        .header(X_USER_ID, USER_ID)
                        .content("{\"membershipId\":\"notExistMembership\",\"membershipName\":\"shinsegaepoint\",\"amount\":5000}"))
                        .andDo(print());
        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.response").isEmpty())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.status").value(404));
    }

}
