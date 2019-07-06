package com.jmx.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "friends_request")
public class FriendsRequest implements Serializable {
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
     * 发送时间
     */
    @Column(name = "request_data_time")
    private Date requestDataTime;

    private static final long serialVersionUID = 1L;
}