package com.jmx.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * WebSocket的服务端,与SpringBoot整合
 * 修改为单例模式启动---静态内部类
 */
@Component
public class WebSocketServer {

    //静态内部类声明
    private static class SingletonWSSever{
        static final WebSocketServer instance = new WebSocketServer();
    }

    //公开获取的静态方法
    public static WebSocketServer getInstance(){
        return SingletonWSSever.instance;
    }

    private NioEventLoopGroup mainGroup;
    private NioEventLoopGroup subGroup;
    private ServerBootstrap bootstrap;
    private ChannelFuture future;

    //构造器私有
    private WebSocketServer(){
        //1. 两个线程组
        mainGroup = new NioEventLoopGroup();
        subGroup = new NioEventLoopGroup();
        //2. 启动类
        bootstrap = new ServerBootstrap();
        //3. 定义启动的线程组,channel和初始化器
        bootstrap.group(mainGroup,subGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WebSocketSeverInitializer());
    }

    //服务器开启的方法
    public void start(){
        this.future = bootstrap.bind(9999);
        System.err.println("Netty Websocket Server 启动完毕");
    }

}
