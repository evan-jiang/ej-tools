package com.ej.tools.dto;

import lombok.Data;

@Data
public class BaseResult<T> {

    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";

    private String code = SUCCESS;
    private T result;

    public BaseResult<T> genSuccess(T result){
        this.code = SUCCESS;
        this.result = result;
        return this;
    }


    public BaseResult<T> genFail(T result){
        this.code = FAIL;
        this.result = result;
        return this;
    }
}
