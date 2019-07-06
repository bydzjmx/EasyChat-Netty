package com.jmx.enums;

import lombok.*;

/**
 * 搜索用户的前置判断, 通过status判定
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PreSearchFriendsEnum {

    SUCCESS(0,"OK"),
    USER_NOT_FOUND(1,"搜索的用户未找到"),
    USER_CAN_NOT_BE_YOURSELF(2,"不能添加自己为好友"),
    USER_ALREADY_BE_FRIEND(3,"搜索的用户已经是你的好友");

    private Integer status;
    private String msg;

    //根据status返回msg
    public static String getMsgByStatus(Integer status){
        for (PreSearchFriendsEnum type : PreSearchFriendsEnum.values()) {
            if (type.getStatus() == status) {
                return type.msg;
            }
        }
        return null;
    }
}
