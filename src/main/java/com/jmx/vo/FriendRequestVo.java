package com.jmx.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 显示发送添加好友请求者的信息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestVo {
    private String senderId;
    private String senderNickname;
    private String senderFaceImage;
    private String senderUsername;

}
