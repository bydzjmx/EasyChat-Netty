package com.jmx.netty;

import com.jmx.enums.MsgActionEnum;
import com.jmx.mapper.UsersMapper;
import com.jmx.pojo.Users;
import com.jmx.push.AppPush;
import com.jmx.push.AsyncCenter;
import com.jmx.service.UsersService;
import com.jmx.service.impl.UsersServiceImpl;
import com.jmx.utils.JsonUtils;
import com.jmx.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.minidev.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义handler,继承简单频道入站处理程序,范围为wen文本套接字Frame
 * websocket间通过frame进行数据的传递和发送
 * 此版本为user与channel绑定的版本，消息会定向发送和接收到指定的user的channel中。
 *
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{
    
    //定义channel集合,管理channel,传入全局事件执行器
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

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
        //2. 对传递过来的消息类型进行判断，不同类型进行不同的逻辑处理，使用枚举类
        DataContent content = JsonUtils.jsonToPojo(text, DataContent.class);
        //获取动作类型
        Integer action = content.getAction();
//        System.out.println(action);
        //获取channel
        Channel currentChannel = ctx.channel();

        //2.1 websocket第一次open时，此时初始化channel，将其与userId一一对应
        if(action == MsgActionEnum.CONNECT.type){
            //将channel与userId放入对应的关系类中
            UserChannelRelation.put(content.getChatData().getSenderId(),currentChannel);

            //测试channelGroup中的channel
//            for (Channel user : users) {
//                System.out.println(user.id().asLongText());
//            }
//            //测试UserChannelRelation中的channel
//            UserChannelRelation.output();

        }else if(action == MsgActionEnum.CHAT.type){
            //System.out.println("进行消息发送逻辑");
            //2.2 聊天类型的消息，此时需要把聊天记录保存到数据库，同时添加为未读状态
            ChatData chatData = content.getChatData();
            String msgText = chatData.getMsg();
            String senderId = chatData.getSenderId();
            String receiverId = chatData.getReceiverId();
            //2.2.2保存到数据库中
            //2.2.3 手动获取userService对象
            UsersServiceImpl service = (UsersServiceImpl)SpringUtil.getBean("usersServiceImpl");
            UsersMapper usersMapper = (UsersMapper) SpringUtil.getBean("usersMapper");
            String msgId = service.saveMsg(chatData);
            //2.2.4 设置msgId推回前台
            chatData.setMsgId(msgId);
            //2.2.5 返回DataContent模型
            DataContent dataContent = new DataContent();
            dataContent.setChatData(chatData);

            //获取发信和收信者昵称
            Users sender = usersMapper.selectByPrimaryKey(senderId);
            Users receiver = usersMapper.selectByPrimaryKey(receiverId);

            //2.3 发送消息,根据channel进行消息推送(接收方),因为receiverId是唯一的,所以只有接收方能收到消息
            Channel receiveChannel = UserChannelRelation.get(receiverId);
            if (receiveChannel == null){
                //TODO 用户不在线, 推送消息到用户APP, (JPush,个推,小米推送等)
                AppPush.sendPush(sender.getNickname(),msgText,receiver.getCid());
                System.out.println("接收方不在线,请稍候重试");
            }else{
                //从ChannelGroup中去查找对应的channel是否存在
                //System.out.println("接收方在线");
                Channel findChannel = ChatHandler.users.find(receiveChannel.id());
                if(findChannel != null){
                    //用户在线, 发送chatData回客户端接收
                    receiveChannel.writeAndFlush(
                            new TextWebSocketFrame(
                                    JsonUtils.objectToJson(dataContent)));
                }else {
                    //用户离线,推送消息
                    AppPush.sendPush(sender.getNickname(),msgText,receiver.getCid());
                }
            }

        }else if(action == MsgActionEnum.SIGNED.type){
            //2.3 签收类型的消息，此时，针对具体的消息id进行签收，修改对应数据库中的牵手状态
            // 签收: 非已读回执, 客户端接收到消息,回发送已收到的回执(不一定要阅读)
            //2.3.1 获取service
            UsersServiceImpl service = (UsersServiceImpl)SpringUtil.getBean("usersServiceImpl");
            //2.3.2 对于签收类型的消息,前台传递过来的msgId在extend字段中
            String msgIdStr = content.getExtend();
            //2.3.3 切分字符串获取msgId数组
            String[] msgIds = msgIdStr.split(",");
            //2.3.4 转换为list
            List<String> msgIdList = new ArrayList<>();
            for (String msgId : msgIds) {
                if(StringUtils.isNoneBlank(msgId)){
                    //非空msgId放入list中
                    msgIdList.add(msgId);
                }
            }
            //2.3.5 调用service方法,对数据进行签收
            service.updateSignMsg(msgIdList);

        }else if(action == MsgActionEnum.KEEPALIVE.type){
            //2.4 心跳类型消息
            System.out.println("收到来自channel为"+currentChannel+"的心跳包");
        }

    }

    /**
     * 当客户端连接服务端之后(打开连接)----->handlerAdded
     * 获取客户端的channel,并且放到ChannelGroup中去管理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
    }

    //处理器移除时,移除channelGroup中的channel
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //打印移除的channel
        String asShortText = ctx.channel().id().asShortText();
        System.out.println("客户端被移除，channelId为：" + asShortText);
        users.remove(ctx.channel());
    }

    /**
     * 发生异常时，关闭连接（channel），随后将channel从ChannelGroup中移除
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("出错啦, 原因是:"+cause.getMessage());
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
