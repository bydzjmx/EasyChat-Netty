package com.jmx.push;

import java.io.IOException;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.style.Style0;

public class AppPush {

    //定义常量, appId、appKey、masterSecret 采用本文档 "第二步 获取访问凭证 "中获得的应用配置
    private static String appId = "93tLbulnarAcJIrMHUkIe3";
    private static String appKey = "Y6ENFV0VZK8KpfKAlfHnv2";
    private static String masterSecret = "JsKBZJdMnn84mSUIfsLuj7";
    private static String url = "http://sdk.open.api.igexin.com/apiex.htm";

    /**
     * 发送推送
     * @param title 推送标题
     * @param text 推送内容
     * @param cid  要推送到的cid
     * @throws IOException
     */
    public static void sendPush(String title, String text, String cid) throws IOException {

        IGtPush push = new IGtPush(url, appKey, masterSecret);

        //建立NotificationTemplate类型消息
        NotificationTemplate template = notificationTemplateDemo(title, text);

        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 3600 * 1000);
        message.setData(template);
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0);
        Target target = new Target();
        target.setAppId(appId);
        //设定客户端id，点对点发送
        target.setClientId(cid);
        //target.setAlias(Alias);
        IPushResult ret;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (ret != null) {
            System.out.println(ret.getResponse().toString());
        } else {
            System.out.println("服务器响应异常");
        }
    }
    
    private static NotificationTemplate notificationTemplateDemo(String title, String text) {
        NotificationTemplate template = new NotificationTemplate();
        // 设置appId与appKey
        template.setAppId(appId);
        template.setAppkey(appKey);
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(1);
        template.setTransmissionContent("文字消息通知");
        // 设置定时展示时间
        // template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle(title);
        style.setText(text);
        // 配置通知栏图标
        style.setLogo("icon.png");
        // 配置通知栏网络图标
        style.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);
        return template;
    }
}
