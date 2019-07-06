package com.jmx.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;

/**
 * 自定义handler,继承简单频道入站处理程序,范围为wen文本套接字Frame
 * websocket间通过frame进行数据的传递和发送
 * 此版本为聊天室版本，即所有的消息会推送到所有连接的客户端（channel中）
 */
public class ChatRoomHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

    //定义channel集合,管理channel,传入全局事件执行器
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 定义信道的消息处理机制,该方法处理一次,故需要同时对所有客户端进行操作(channelGroup)
     * @param ctx 上下文
     * @param msg 文本消息
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //1. 获取客户端传递过来的消息,其对象为TextWebSocketFrame
        String text = msg.text();
        System.out.println("接收到数据为: "+ text);
        //2. 写入缓冲区并发回给所有的客户端(通过所有的channel,故需要对所有channel进行集中管理,引入channelGroup)
//        for (Channel channel : clients) {
//            //writeAndFlush,写到buffer区并返回给客户端
////            channel.writeAndFlush(new TextWebSocketFrame(
////                    "[服务器在]" + LocalDateTime.now()+"接收到消息,消息为: "+ text
////            ));
//        }
        //以下表达方法效果一致
        clients.writeAndFlush(new TextWebSocketFrame(
                "[服务器在]" + LocalDateTime.now()+"接收到消息,消息为: "+ text
        ));
    }

    /**
     * 当客户端连接服务端之后(打开连接)----->handlerAdded
     * 获取客户端的channel,并且放到ChannelGroup中去管理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    //处理器移除时,移除channelGroup中的channel
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        clients.remove(ctx.channel());
        //打印channel对应的长id和短id
        System.out.println("客户端断开,channel对应的长id为: "+ctx.channel().id().asLongText());
        System.out.println("客户端断开,channel对应的短id为: "+ctx.channel().id().asShortText());
    }
}
