package com.jmx.netty;

import com.jmx.pojo.ChatMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据内容，作为前台和后台，进行消息发送和接收的载体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataContent implements Serializable {

    //动作类型,参考消息类型的枚举
    private Integer action;
    //传递过来的消息
    private ChatData chatData;
    //扩展字段
    private String extend;
}
