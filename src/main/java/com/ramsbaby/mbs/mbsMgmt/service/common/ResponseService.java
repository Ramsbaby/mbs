package com.ramsbaby.mbs.mbsMgmt.service.common;

import com.ramsbaby.mbs.mbsMgmt.model.common.response.CommonResult;
import com.ramsbaby.mbs.mbsMgmt.model.common.response.ListResult;
import com.ramsbaby.mbs.mbsMgmt.model.common.response.SingleResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ResponseService {

    // enum으로 api 요청 결과에 대한 code, message를 정의합니다.
    public enum CommonResponse {
        SUCCESS(200, "성공하였습니다.");

        int code;
        String msg;
        List response;

        CommonResponse(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public List getList() {
            return response;
        }
    }

    // 단일건 결과를 처리하는 메소드
    public <T> SingleResult<T> getSingleResult(T data) {
        SingleResult<T> result = new SingleResult<>();
        result.setResponse(data);
        setSuccessResultData(result);
        return result;
    }

    // 다중건 결과를 처리하는 메소드
    public <T> ListResult<T> getListResult(List<T> list) {
        ListResult<T> result = new ListResult<>();
        result.setResponse(list);
        setSuccessResultList(result);
        return result;
    }

    // 멤버십 삭제 성공 결과만 처리하는 메소드
    public CommonResult getSuccessPutResult() {
        CommonResult result = new CommonResult();
        result.setResponse(true);
        setSuccessResultData(result);
        return result;
    }

    // 실패 결과만 처리하는 메소드
    public <T> CommonResult<T> getFailResult(T code, T msg) {
        CommonResult result = new CommonResult();
        result.setSuccess(false);
        result.setResponse(null);

        HashMap<String, T> errorMap = new HashMap<>();
        errorMap.put("message", msg);
        errorMap.put("status", code);

        result.setError(errorMap);
        return result;
    }

    // 결과 모델에 api 요청 성공 다중 데이터를 세팅해주는 메소드
    private void setSuccessResultList(CommonResult result) {
        result.setSuccess(true);
        result.setResponse(CommonResponse.SUCCESS.getList());
        result.setError(null);
    }

    // 결과 모델에 api 요청 성공 단일 데이터를 세팅해주는 메소드
    private void setSuccessResultData(CommonResult result) {
        result.setSuccess(true);
//        result.setResponse(CommonResponse.SUCCESS.getList());
        result.setError(null);
    }
}
