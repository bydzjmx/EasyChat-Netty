package com.jmx.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据库的pojo类
 */

@Data
@Table(name = "chat_msg")
public class ChatMsg implements Serializable {
    /**
     * ID
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 发送人id
     */
    @Column(name = "send_user_id")
    private String sendUserId;

    /**
     * 接收人id
     */
    @Column(name = "accept_user_id")
    private String acceptUserId;

    /**
     * 消息内容
     */
    @Column(name = "msg")
    private String msg;

    /**
     * 是否已读
     */
    @Column(name = "sign_flag")
    private Boolean signFlag;

    /**
     * 消息创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}