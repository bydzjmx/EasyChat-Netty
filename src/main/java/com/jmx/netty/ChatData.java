package com.jmx.netty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息数据载体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatData implements Serializable {

    //发送者id
    private String senderId;
    //接收者id
    private String receiverId;
    //消息的主体
    private String msg;
    //消息的id,存于消息记录中,对应于数据库的消息的id
    private String msgId;
}
