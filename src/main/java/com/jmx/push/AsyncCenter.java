package com.jmx.push;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 异步发送推送消息
 */
@Component
public class AsyncCenter {

    @Async
    public void sendPush(String title, String text, String cid){
        try {
            AppPush.sendPush(title,text,cid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
