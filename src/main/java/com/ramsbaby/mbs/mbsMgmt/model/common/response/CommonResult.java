package com.ramsbaby.mbs.mbsMgmt.model.common.response;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class CommonResult<T> {

    private boolean success;

    private T response;

    private HashMap<String, T> error;
}

