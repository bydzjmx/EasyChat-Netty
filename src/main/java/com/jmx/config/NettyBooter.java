package com.jmx.config;

import com.jmx.netty.WebSocketServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 当SpringBoot启动后,加载这个类
 */
@Component
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //事件获得上下文对象化后启动服务器
        if(event.getApplicationContext().getParent() == null){
            WebSocketServer.getInstance().start();
        }
    }
}
