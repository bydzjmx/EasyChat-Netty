package com.jmx.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Users的BO对象,用于前后端上传图片
 * 前端传入base64代码串
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersBo {
    private String userId;
    private String faceData;
    private String nickname;
}
