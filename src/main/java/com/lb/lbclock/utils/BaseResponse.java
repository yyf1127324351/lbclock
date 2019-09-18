package com.lb.lbclock.utils;

public class BaseResponse<T> {


    private String code;

    private String message;
    // 自定义返回数据
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public BaseResponse() {
        this.code = BaseResponse.ResponseCode.SUCCESS.value;
        this.message = BaseResponse.ResponseCode.SUCCESS.description;
    }

    public BaseResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<T>(BaseResponse.ResponseCode.SUCCESS.value, BaseResponse.ResponseCode.SUCCESS.description, null);
    }

    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<T>(BaseResponse.ResponseCode.SUCCESS.value, message, null);
    }
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(BaseResponse.ResponseCode.SUCCESS.value, BaseResponse.ResponseCode.SUCCESS.description, data);
    }
    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<T>(BaseResponse.ResponseCode.SUCCESS.value, message, data);
    }
    public static <T> BaseResponse<T> error() {
        return new BaseResponse<T>(BaseResponse.ResponseCode.SYS_ERROR.value, BaseResponse.ResponseCode.SYS_ERROR.description, null);
    }
    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<T>(BaseResponse.ResponseCode.SYS_ERROR.value, message, null);
    }
    public static <T> BaseResponse<T> paramEerror() {
        return new BaseResponse<T>(BaseResponse.ResponseCode.PARAM_CHECK_FAIL.value, BaseResponse.ResponseCode.PARAM_CHECK_FAIL.description, null);
    }
    public static <T> BaseResponse<T> paramEerror(String message) {
        return new BaseResponse<T>(BaseResponse.ResponseCode.PARAM_CHECK_FAIL.value, message, null);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public enum ResponseCode{

        SUCCESS("00","success"),

        PARAM_CHECK_FAIL("01","request params check fail"),

        LOGIN_CHECK_FAIL("02","login check fail"),

        FREQUENT_REQUEST("03", "frequent request"),

        BIZ_CHECK_FAIL("04", "biz check fail"),

        SYS_ERROR("99","system error");


        private String value;
        private String description;

        ResponseCode(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

    }


}
