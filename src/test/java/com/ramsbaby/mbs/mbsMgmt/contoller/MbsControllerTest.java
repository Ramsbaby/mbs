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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.ramsbaby.mbs.mbsMgmt.ApiDocumentUtil.getDocumentRequest;
import static com.ramsbaby.mbs.mbsMgmt.ApiDocumentUtil.getDocumentResponse;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
public class MbsControllerTest {
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

        //????????? ????????? ?????? (??????????????? : ?????????) - MEMBERSHIP_ID1
        MembershipDetailEntity membershipDetailEntity = MembershipDetailEntity.
                create(membershipRepository.findById(MEMBERSHIP_ID1).get(),
                        membershipUserRepository.findById(USER_ID).get(),
                        POINT, "Y");
        membershipDetailRepository.saveAndFlush(membershipDetailEntity);


        //????????? ????????? ?????? (??????????????? : ?????????) - MEMBERSHIP_ID2
        MembershipDetailEntity membershipDetailEntity2 = MembershipDetailEntity.
                create(membershipRepository.findById(MEMBERSHIP_ID2).get(),
                        membershipUserRepository.findById(USER_ID).get(),
                        POINT, "Y");
        membershipDetailRepository.saveAndFlush(membershipDetailEntity2);

    }

    @Test
    @DisplayName("???????????????")
    void registMembership() throws Exception {
        // given
        // 'test1'??? ????????? MEMBERSHIP_ID1('shinsegae'),MEMBERSHIP_ID2('spc'),MEMBERSHIP_ID3('cj')??? ?????? ????????? ?????? ??????

        // when
        // ???????????????('shinsegae', 'spc', 'cj')??? ???????????? ??????
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .post("/api/v1/membership/")
                .header("Content-Type", "application/json")
                .header(X_USER_ID, USER_ID)
                .content("{\"membershipId\":\"cj\",\"membershipName\":\"cjone\",\"point\":5000}"))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document(
                        "registMembership",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(X_USER_ID).description("????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("membershipId").type(JsonFieldType.STRING).description("????????? ????????? ID"),
                                fieldWithPath("membershipName").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                fieldWithPath("point").type(JsonFieldType.NUMBER).description("?????? ?????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("????????????"),
                                fieldWithPath("response[].seq").type(JsonFieldType.NUMBER).description("SEQUENCE"),
                                fieldWithPath("response[].userId").type(JsonFieldType.STRING).description("????????? ID"),
                                fieldWithPath("response[].membershipId").type(JsonFieldType.STRING).description("????????? ID"),
                                fieldWithPath("response[].membershipName").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("response[].startDate").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("response[].membershipStatus").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("response[].point").type(JsonFieldType.NUMBER).description("????????? ??????")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("?????????????????????")
    void getMembershipAll() throws Exception {
        // given
        // 'test1'??? ????????? MEMBERSHIP_ID1('shinsegae'),MEMBERSHIP_ID2('spc'),MEMBERSHIP_ID3('cj')??? ?????? ????????? ?????? ??????

        // when
        // 'test1'??? ????????? ?????? ?????????('shinsegae', 'spc', 'cj')??? ???????????? ?????? ??????
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/api/v1/membership/")
                .header("Content-Type", "application/json")
                .header(X_USER_ID, USER_ID))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document(
                        "getMembershipAll",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(X_USER_ID).description("????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("????????????"),
                                fieldWithPath("response[].seq").type(JsonFieldType.NUMBER).description("SEQUENCE"),
                                fieldWithPath("response[].userId").type(JsonFieldType.STRING).description("????????? ID"),
                                fieldWithPath("response[].membershipId").type(JsonFieldType.STRING).description("????????? ID"),
                                fieldWithPath("response[].membershipName").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("response[].startDate").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("response[].membershipStatus").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("response[].point").type(JsonFieldType.NUMBER).description("????????? ??????")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("?????????????????????")
    void getMembershipDetail() throws Exception {
        // given

        // when
        // 'test1'??? ????????? ?????????('spc')??? ???????????? ?????? ??????
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/api/v1/membership/{membershipId}", MEMBERSHIP_ID1)
                .header("Content-Type", "application/json")
                .header(X_USER_ID, USER_ID))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document(
                        "getMembershipDetail",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(X_USER_ID).description("????????? ID")
                        ),
                        pathParameters(parameterWithName("membershipId").description("????????? ID")),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("????????????"),
                                fieldWithPath("response.seq").type(JsonFieldType.NUMBER).description("SEQUENCE"),
                                fieldWithPath("response.userId").type(JsonFieldType.STRING).description("????????? ID"),
                                fieldWithPath("response.membershipId").type(JsonFieldType.STRING).description("????????? ID"),
                                fieldWithPath("response.membershipName").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("response.startDate").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("response.membershipStatus").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("response.point").type(JsonFieldType.NUMBER).description("????????? ??????")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("???????????????")
    void deleteMembership() throws Exception {
        // given
        // when
        // 'test1'??? ????????? ?????????('spc')??? ???????????? ?????? ??????
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .delete("/api/v1/membership/{membershipId}", MEMBERSHIP_ID1)
                .header("Content-Type", "application/json")
                .header(X_USER_ID, USER_ID))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document(
                        "deleteMembership",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(X_USER_ID).description("????????? ID")
                        ),
                        pathParameters(parameterWithName("membershipId").description("????????? ID")),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("response").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("????????????")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("???????????????")
    void earnPoint() throws Exception {
        // given
        // 'test1'??? ????????? MEMBERSHIP_ID1('shinsegae'),MEMBERSHIP_ID2('spc'),MEMBERSHIP_ID3('cj')??? ?????? ????????? ?????? ??????

        // when
        // ???????????????('shinsegae', 'spc', 'cj')??? ???????????? ??????
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .put("/api/v1/membership/point")
                .header("Content-Type", "application/json")
                .header(X_USER_ID, USER_ID)
                .content("{\"membershipId\":\"spc\",\"amount\":5000}"))
                .andDo(MockMvcResultHandlers.print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(document(
                        "earnPoint",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(X_USER_ID).description("????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("membershipId").type(JsonFieldType.STRING).description("????????? ????????? ID"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("response").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("????????????")
                        )
                ))
        ;
    }
}
