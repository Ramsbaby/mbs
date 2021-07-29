package com.ramsbaby.mbs.mbsMgmt.controller.common;

import com.ramsbaby.mbs.mbsMgmt.model.common.response.CommonResult;
import com.ramsbaby.mbs.mbsMgmt.service.common.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController {
    @Autowired
    ResponseService responseService;

    @Autowired
    MessageSource messageSource;

    //404 에러 처리
    @RequestMapping(value = "/error")
    public ResponseEntity<CommonResult> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(responseService.getFailResult(HttpStatus.NOT_FOUND.value(), getMessage("resourceNotExist.msg")));
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseService.getFailResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), getMessage("unKnown.msg")));
    }

    // code정보에 해당하는 메시지를 조회합니다.
    private String getMessage(String code) {
        return getMessage(code, null);
    }

    // code정보, 추가 argument로 현재 locale에 맞는 메시지를 조회합니다.
    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
