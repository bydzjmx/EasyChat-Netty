package com.jmx.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 配置从线程池的助手类初始化器
 */

public class WebSocketSeverInitializer extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel channel) throws Exception {
        //1. 获取pipeline
        ChannelPipeline pipeline = channel.pipeline();
        //2. 配置handler
        //2.1 websocket基于http协议,需要http编码和解码工具
        pipeline.addLast("HttpServerCodec",new HttpServerCodec());
        //2.2 对于大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        //2.3 对于http的消息进行聚合,聚合成FullHttpRequest或者FullHttpResponse,几乎所有netty都需要此handler
        pipeline.addLast(new HttpObjectAggregator(1024*64));

        //=========================以上是用于支持http协议============================

        //=========================以下是用于支持心跳============================
        //针对客户端，如果1分钟之内没有向服务器发送读写心跳，则主动断开
        pipeline.addLast(new IdleStateHandler(40,50,60));
        //自定义的读写空闲状态检测
        pipeline.addLast(new HeartBeatHandler());
        //=========================以下是用于支持websocket协议============================
        /**
         * websocket服务器处理协议,用于指定给客户端访问的路由:/ws
         * 同时该handler会自动处理一些复杂的事情,如握手动作,handshaking ( close + ping + pong )
         * ping+pong组合成心跳
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //定义自己的handler,主要是对请求进行处理和发送
        pipeline.addLast(new ChatHandler());
    }
}
