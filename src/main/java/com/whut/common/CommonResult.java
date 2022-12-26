package com.whut.common;

import java.io.Serializable;

/**
 * 统一返回结果
 */
public class CommonResult implements Serializable {

    private static final long serialVersionUID = 6439646269084700779L;

    private int code = 0;

    private String message;

    private Object data;

    private int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean hasError(){
        return this.code != 0;
    }

    public CommonResult addError(String message){
        this.message = message;
        this.code = 1;

        return this;
    }

    public CommonResult addConfirmError(String message){
        this.message = message;
        this.code = 2;
        return this;
    }

    public CommonResult success(Object data){
        this.data = data;
        this.code = 0;
        return this;
    }

}
