package com.jmx.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "users")
public class Users implements Serializable {
    /**
     * ID
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 用户名
     */
    @Column(name = "username")
    private String username;

    /**
     * 密码，已加密
     */
    @Column(name = "`password`")
    private String password;

    /**
     * 用户头像
     */
    @Column(name = "face_image")
    private String faceImage;

    /**
     * 用户大头像
     */
    @Column(name = "face_image_big")
    private String faceImageBig;

    /**
     * 用户昵称
     */
    @Column(name = "nickname")
    private String nickname;

    /**
     * 用户二维码
     */
    @Column(name = "qrcode")
    private String qrcode;

    /**
     * 用户客户端id
     */
    @Column(name = "cid")
    private String cid;

    private static final long serialVersionUID = 1L;
}