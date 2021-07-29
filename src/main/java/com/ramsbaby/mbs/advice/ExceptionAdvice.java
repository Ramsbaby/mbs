package com.ramsbaby.mbs.advice;

import com.ramsbaby.mbs.advice.exception.*;
import com.ramsbaby.mbs.mbsMgmt.model.common.response.CommonResult;
import com.ramsbaby.mbs.mbsMgmt.service.common.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;
    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("unKnown.code")), getMessage("unKnown.msg") + "(" + e.getMessage() + ")");
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult forbiddenWordException(HttpServletRequest request, RuntimeException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("forbiddenWord.code")), getMessage("forbiddenWord.msg", new Object[]{e.getMessage()}));
    }

    //멤버십 상세조회 - 멤버십 디테일 없음 에러
    @ExceptionHandler(CMembershipDetailNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult membershipDetailNotExistException(HttpServletRequest request, CMembershipDetailNotExistException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("membershipDetailNotExist.code")), getMessage("membershipDetailNotExist.msg"));
    }

    //유저 없음 에러
    @ExceptionHandler(CMembershipUserNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult membershipUserExistException(HttpServletRequest request, CMembershipUserNotExistException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("membershipUserNotExist.code")), getMessage("membershipUserNotExist.msg"));
    }

    //멤버십 없음 에러
    @ExceptionHandler(CMembershipNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult membershipNotExistException(HttpServletRequest request, CMembershipNotExistException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("membershipNotExist.code")), getMessage("membershipNotExist.msg"));
    }

    //멤버십 중복 에러
    @ExceptionHandler(CMembershipDetailIsExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult membershipNotExistException(HttpServletRequest request, CMembershipDetailIsExistException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("membershipDetailIsExist.code")), getMessage("membershipDetailIsExist.msg"));
    }

    //멤버십 비활성화 상태
    @ExceptionHandler(CMembershipStatusDisabledException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResult membershipNotExistException(HttpServletRequest request, CMembershipStatusDisabledException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("membershipStatusDisabledExist.code")), getMessage("membershipStatusDisabledExist.msg"));
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
