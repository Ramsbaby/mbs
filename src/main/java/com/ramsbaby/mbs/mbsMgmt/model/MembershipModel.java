package com.ramsbaby.mbs.mbsMgmt.model;

import com.ramsbaby.mbs.mbsMgmt.entity.MembershipDetailEntity;
import com.ramsbaby.mbs.mbsMgmt.entity.MembershipUserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@Builder
public class MembershipModel {
    private List<MembershipDetailModel> membershipDetailModels;
    private MembershipDetailModel membershipDetailModel;

    public static MembershipModel setEntity(MembershipUserEntity userEntity) {
        return MembershipModel.builder()
                .membershipDetailModels(userEntity.getMembershipDetails().stream().map(MembershipDetailModel::setEntity).collect(toList()))
                .build();
    }

    @Builder
    @Getter
    public static class MembershipDetailModel {
        private Long seq;
        private String membershipId;
        private String userId;
        private String membershipName;
        private LocalDateTime startDate;
        private String membershipStatus;
        private Long point;

        public static MembershipDetailModel setEntity(MembershipDetailEntity membershipDetailEntity) {
            return MembershipDetailModel.builder()
                    .seq(membershipDetailEntity.getSeq())
                    .membershipId(membershipDetailEntity.getMembershipId().getMembershipId())
                    .userId(membershipDetailEntity.getUser().getUserId())
                    .membershipName(membershipDetailEntity.getMembershipId().getMembershipName())
                    .startDate(membershipDetailEntity.getStartDate())
                    .membershipStatus(membershipDetailEntity.getMembershipStatus())
                    .point(membershipDetailEntity.getPoint())
                    .build();
        }

        //포인트 적립을 위한 파라미터
        @Builder
        @Getter
        public static class EarnPointParam {
            private String membershipId;
            private String membershipName;
            private Long amount;

            public static EarnPointParam of(String membershipId, Long amount) {
                return new EarnPointParam(membershipId, "", amount);
            }

            public static EarnPointParam of(String membershipId, String membershipName, Long amount) {
                return new EarnPointParam(membershipId, membershipName, amount);
            }
        }
    }

    //멤버십 등록을 위한 파라미터
    @Builder
    @Getter
    public static class MembershipRegistParam {
        private String membershipId;
        private String membershipName;
        private Long point;

        public static MembershipRegistParam of(String membershipId, String membershipName, Long point) {
            return new MembershipRegistParam(membershipId, membershipName, point);
        }
    }

}