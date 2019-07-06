package com.jmx.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "my_friends")
public class MyFriends implements Serializable {
    /**
     * ID
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 当前用户id
     */
    @Column(name = "my_user_id")
    private String myUserId;

    /**
     * 添加朋友的id
     */
    @Column(name = "my_friend_user_id")
    private String myFriendUserId;

    private static final long serialVersionUID = 1L;
}