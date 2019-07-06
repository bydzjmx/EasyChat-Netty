package com.jmx.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Users的VO对象,用于返回给前端
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersVo  {
    private String id;
    private String username;
    private String faceImage;
    private String faceImageBig;
    private String nickname;
    private String qrcode;
}
