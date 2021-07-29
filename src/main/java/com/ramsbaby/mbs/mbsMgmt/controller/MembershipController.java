package com.ramsbaby.mbs.mbsMgmt.controller;

import com.ramsbaby.mbs.mbsMgmt.model.MembershipModel;
import com.ramsbaby.mbs.mbsMgmt.model.MembershipUserModel;
import com.ramsbaby.mbs.mbsMgmt.model.common.response.CommonResult;
import com.ramsbaby.mbs.mbsMgmt.model.common.response.ListResult;
import com.ramsbaby.mbs.mbsMgmt.model.common.response.SingleResult;
import com.ramsbaby.mbs.mbsMgmt.service.mbs.MembershipServiceImpl;
import com.ramsbaby.mbs.mbsMgmt.service.common.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/membership")
public class MembershipController {
    private static final String X_USER_ID = "X-USER-ID";
    private final ResponseService responseService;
    private final MembershipServiceImpl membershipService;

    //1.전체조회 - X_USER_ID를 기반으로 모든 멤버십상태조회
    @GetMapping("")
    public ListResult<MembershipModel.MembershipDetailModel> getMembershipAll(@RequestHeader(X_USER_ID) String userId) {
        MembershipUserModel membershipUserModel = MembershipUserModel.of(userId);
        List<MembershipModel.MembershipDetailModel> membershipDeatils = membershipService.getMembershipAll(membershipUserModel);
        return responseService.getListResult(membershipDeatils);
    }

    //2.멤버십 등록
    @PostMapping
    public ListResult<MembershipModel.MembershipDetailModel> registMembership(@RequestHeader(X_USER_ID) String userId,
                                                                              @RequestBody MembershipModel.MembershipRegistParam param) {
        MembershipUserModel membershipUserModel = MembershipUserModel.of(userId);
        membershipUserModel = membershipService.registMembership(membershipUserModel, param);
        List<MembershipModel.MembershipDetailModel> membershipDeatils = membershipService.getMembershipAll(membershipUserModel);
        return responseService.getListResult(membershipDeatils);
    }

    //3.멤버십 삭제(비활성화)
    @DeleteMapping("/{membershipId}")
    public CommonResult deleteMembership(@RequestHeader(X_USER_ID) String userId,
                                         @PathVariable String membershipId) {
        membershipService.deleteMembership(userId, membershipId);
        return responseService.getSuccessPutResult();
    }

    //4.멤버십 상세조회 - X_USER_ID(헤더), membershipId(parameter)를 기반으로 멤버십 상세조회
    @GetMapping("/{membershipId}")
    public SingleResult getMembershipDetail(@RequestHeader(X_USER_ID) String userId,
                                            @PathVariable String membershipId) {
        MembershipModel.MembershipDetailModel membershipDetail = membershipService.getMembershipDetail(userId, membershipId);
        return responseService.getSingleResult(membershipDetail);
    }

    //5.포인트 적립
    @PutMapping("/point")
    public CommonResult earnPoint(@RequestHeader(X_USER_ID) String userId,
                                  @RequestBody MembershipModel.MembershipDetailModel.EarnPointParam param) {
        membershipService.earnPoint(userId, param);
        return responseService.getSuccessPutResult();
    }

}
