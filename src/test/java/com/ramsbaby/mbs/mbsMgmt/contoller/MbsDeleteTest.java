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
public class MbsDeleteTest {
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
    @PersistenceContext    // EntityManagerFactory??? DI ??? ??? ????????? ??????????????? ??????
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() throws Exception {
        //??????, ?????????????????? ??????
        MembershipUserEntity membershipUserEntity = MembershipUserEntity.create(USER_ID);
        membershipUserRepository.saveAndFlush(membershipUserEntity.builder().userId(USER_ID).build());

        //????????? ???????????? ?????????
        entityManager.clear();
        entityManager.close();

        //????????? ????????? ??????
        MembershipDetailEntity membershipDetailEntity = MembershipDetailEntity.
                create(membershipRepository.findById(MEMBERSHIP_ID1).get(),
                        membershipUserRepository.findById(USER_ID).get(),
                        POINT, "Y");
        membershipDetailRepository.saveAndFlush(membershipDetailEntity);
    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? - ??????")
    void ????????????????????????() throws Exception {
        //given
        //USER_ID ????????? MEMBERSHIP_ID1??? ????????? ???????????? ???????????? ??????

        //when
        //'test1'??? ????????? MEMBERSHIP_ID1??? ????????? ????????? ????????????????????? ??????
        ResultActions resultActions =
                mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/membership/" + MEMBERSHIP_ID1)
                        .header(X_USER_ID, USER_ID))
                        .andDo(print());
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? - ????????? ???????????? ?????? ?????? - ??????")
    void ???????????????_???????????????????????????_Exception() throws Exception {
        // given
        // USER_ID ????????? MEMBERSHIP_ID1??? ????????? ???????????? ???????????? ??????

        // when
        //'test1'??? ????????? MEMBERSHIP_ID2??? ????????? ????????? ?????????????????????.
        ResultActions resultActions =
                mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/membership/" + MEMBERSHIP_ID2)
                        .header(X_USER_ID, USER_ID))
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
