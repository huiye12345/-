package com.community.enums;

public enum UserType {
    SPECIAL(1, "特殊人群"),
    VOLUNTEER(2, "志愿者"),
    ADMIN(3, "社区管理员");
    
    private final Integer code;
    private final String desc;
    
    UserType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static UserType getByCode(Integer code) {
        for (UserType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
