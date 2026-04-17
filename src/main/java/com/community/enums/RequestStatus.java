package com.community.enums;

public enum RequestStatus {
    PENDING(0, "待接单"),
    ACCEPTED(1, "已接单"),
    IN_PROGRESS(2, "服务中"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");
    
    private final Integer code;
    private final String desc;
    
    RequestStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static RequestStatus getByCode(Integer code) {
        for (RequestStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
