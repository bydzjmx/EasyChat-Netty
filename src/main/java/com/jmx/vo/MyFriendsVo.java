package com.jmx.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 显示我的好友的信息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyFriendsVo {
    private String friendId;
    private String friendNickname;
    private String friendFaceImage;
    private String friendUsername;
}
