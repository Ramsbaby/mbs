package com.ramsbaby.mbs.mbsMgmt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MembershipUserModel {
    private String userId;

    public static MembershipUserModel of(String userId) {
        return new MembershipUserModel(userId);
    }

}
