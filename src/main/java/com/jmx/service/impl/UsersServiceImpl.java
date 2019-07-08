package com.jmx.service.impl;

import com.jmx.enums.MsgActionEnum;
import com.jmx.enums.PreSearchFriendsEnum;
import com.jmx.mapper.*;
import com.jmx.netty.ChatData;
import com.jmx.netty.DataContent;
import com.jmx.netty.UserChannelRelation;
import com.jmx.pojo.ChatMsg;
import com.jmx.pojo.FriendsRequest;
import com.jmx.pojo.MyFriends;
import com.jmx.pojo.Users;
import com.jmx.push.AppPush;
import com.jmx.push.AsyncCenter;
import com.jmx.service.UsersService;
import com.jmx.utils.FastDFSClient;
import com.jmx.utils.FileUtils;
import com.jmx.utils.JsonUtils;
import com.jmx.utils.QRCodeUtils;
import com.jmx.vo.FriendRequestVo;
import com.jmx.vo.MyFriendsVo;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class UsersServiceImpl implements UsersService{

    private final Sid sid;
    private final UsersMapper usersMapper;
    private final MyFriendsMapper myFriendsMapper;
    private final QRCodeUtils qrCodeUtils;
    private final FastDFSClient fastDFSClient;
    private final FriendsRequestMapper friendsRequestMapper;
    private final UsersMapperCustom usersMapperCustom;
    private final ChatMsgMapper chatMsgMapper;
    private final AsyncCenter asyncCenter;

    public UsersServiceImpl(Sid sid, UsersMapper usersMapper, MyFriendsMapper myFriendsMapper, QRCodeUtils qrCodeUtils, FastDFSClient fastDFSClient, FriendsRequestMapper friendsRequestMapper, UsersMapperCustom usersMapperCustom, ChatMsgMapper chatMsgMapper, AsyncCenter asyncCenter) {
        this.sid = sid;
        this.usersMapper = usersMapper;
        this.myFriendsMapper = myFriendsMapper;
        this.qrCodeUtils = qrCodeUtils;
        this.fastDFSClient = fastDFSClient;
        this.friendsRequestMapper = friendsRequestMapper;
        this.usersMapperCustom = usersMapperCustom;
        this.chatMsgMapper = chatMsgMapper;
        this.asyncCenter = asyncCenter;
    }

    //判断用户名是否存在
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users users = new Users();
        users.setUsername(username);
        Users user = usersMapper.selectOne(users);
        //返回布尔值
        return user != null;
    }

    //用户登录逻辑,返回Users
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String pwd) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        //进行用户名和密码比对
        criteria.andEqualTo("username",username).andEqualTo("password",pwd);
        //进行查询
        return usersMapper.selectOneByExample(example);
    }

    //用户注册,将用户输入的账号和密码,当做新用户信息注册
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users saveUser(Users user) {
        //1. 生成userId
        String id = sid.nextShort();
        //2. 填充用户信息
        user.setId(id);
        //为用户设置默认头像
        user.setFaceImage("M00/00/00/wKgABF0isKOAMcn1AAYEqr08yNY440_80x80.png");
        user.setFaceImageBig("M00/00/00/wKgABF0isKOAMcn1AAYEqr08yNY440.png");
        //3. 注册时为用户生成唯一的二维码
        //3.1 定义二维码生成路径
        //win下的临时目录
        //String urlLocalPath = "d:\\" + user.getId() + "qrcode.png";
        String urlLocalPath = File.separator + "home" + File.separator + "tempImg"
                              + File.separator + user.getId() + "qrcode.png";
        String qrcodeContent = "easyChat_qrcode:" + user.getUsername();
        //3.2 通过工具生成二维码
        qrCodeUtils.createQRCode(urlLocalPath,qrcodeContent);
        //传入数据库（转换为Multipart）
        MultipartFile file = FileUtils.fileToMultipart(urlLocalPath);
        String qrcodeUrl = "";
        try {
            qrcodeUrl = fastDFSClient.uploadQRCode(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //3.3 写入数据库
        user.setQrcode(qrcodeUrl);

        //4. 写入数据库
        int insert = usersMapper.insert(user);
        return insert == 1 ? user : null;
    }

    //更新用户信息
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUsersInfo(Users users) {
        usersMapper.updateByPrimaryKeySelective(users);
        //查询最新的用户信息，并返回
        return queryUsersInfo(users.getId());
    }

    //搜索好友的前置
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer preSearchFriends(String myUserId, String username) {
        Users user = queryUserByUsername(username);
        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        if(user == null){
            return PreSearchFriendsEnum.USER_NOT_FOUND.getStatus();
        }
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        if( myUserId.equals(user.getId())){
            return PreSearchFriendsEnum.USER_CAN_NOT_BE_YOURSELF.getStatus();
        }
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        String friendId = user.getId();
        Example example = new Example(MyFriends.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("myUserId",myUserId);
        criteria.andEqualTo("myFriendUserId",user.getId());
        //数据库中查询
        MyFriends myFriend = myFriendsMapper.selectOneByExample(example);
        if(myFriend == null){
            //对方还不是你的朋友,可以添加
            return PreSearchFriendsEnum.SUCCESS.getStatus();
        }else {
            //对方已经是你的朋友
            return PreSearchFriendsEnum.USER_ALREADY_BE_FRIEND.getStatus();
        }
    }

    //通过用户名查询用户信息
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users queryUserByUsername(String username) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        //如果未找到,返回null,找到了, 返回user对象
        return usersMapper.selectOneByExample(example);
    }

    //发送添加好友请求
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendAddFriendRequest(String myUserId, String username) {
        //1. 获取接收者的id
        Users users = queryUserByUsername(username);
        String friendId = users.getId();
        //2. 检查数据库中是否有相同记录
        Boolean request = queryFriendRequest(myUserId, friendId);
        if(request){
            //如果不是你的好友，并且好友记录没有添加，则新增好友请求记录,添加进数据库
            String requestId = sid.nextShort();

            FriendsRequest friendsRequest = new FriendsRequest();
            friendsRequest.setId(requestId);
            friendsRequest.setSendUserId(myUserId);
            friendsRequest.setAcceptUserId(friendId);
            friendsRequest.setRequestDataTime(new Date());

            friendsRequestMapper.insert(friendsRequest);
        }
        //异步发送推送消息
        asyncCenter.sendPush("好友请求","您收到新的好友请求",users.getCid());
    }

    //查询添加者列表
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendRequestVo> queryFriendRequestList(String acceptUserId) {
        return usersMapperCustom.queryFriendRequestList(acceptUserId);
    }

    //删除好友添加请求
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteFriendRequest(String senderId, String acceptUserId) {
        Example example = new Example(FriendsRequest.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId",senderId);
        criteria.andEqualTo("acceptUserId",acceptUserId);
        //写入数据库
        friendsRequestMapper.deleteByExample(example);
    }

    //通过好友添加请求
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void passFriendRequest(String senderId, String acceptUserId) {
        //1. 生成好友表
        saveFriends(senderId,acceptUserId);
        //2. 逆向生成好友表
        saveFriends(acceptUserId,senderId);
        //3. 删除好友请求表
        deleteFriendRequest(senderId,acceptUserId);

        //4. 使用websocket推送消息
        Channel channel = UserChannelRelation.get(senderId);
        if(channel != null){
            //5. 使用websocket主动推送消息到请求发送者,更新他的通讯录
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);
            channel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
        }
    }

    //查询我的朋友
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVo> queryMyFriends(String userId) {
        return usersMapperCustom.queryMyFriends(userId);
    }

    //保存客户端发送过来的消息到数据库
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveMsg(ChatData chatData) {
        ChatMsg chatMsg = new ChatMsg();
        String msgId = sid.nextShort();
        chatMsg.setId(msgId);
        chatMsg.setSendUserId(chatData.getSenderId());
        chatMsg.setAcceptUserId(chatData.getReceiverId());
        chatMsg.setMsg(chatData.getMsg());
        chatMsg.setCreateTime(new Date());
        chatMsg.setSignFlag(false);
        chatMsgMapper.insert(chatMsg);

        return msgId;
    }

    //批量签收消息
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateSignMsg(List<String> msgIdList) {
        //调用自编写的sql进行批量修改
        usersMapperCustom.updateSignMsg(msgIdList);
    }

    //查询未读消息
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ChatMsg> queryUnReadMsg(String acceptUserId) {
        Example example = new Example(ChatMsg.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("acceptUserId",acceptUserId);
        criteria.andEqualTo("signFlag",false);
        List<ChatMsg> msgs = chatMsgMapper.selectByExample(example);
        return msgs;
    }

    //查询最新的用户信息
    private Users queryUsersInfo(String userId){
        return usersMapper.selectByPrimaryKey(userId);
    }

    //检查添加好友请求数据库中是否有相同记录
    private Boolean queryFriendRequest(String userId, String friendId){
        Example example = new Example(FriendsRequest.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId",userId);
        criteria.andEqualTo("acceptUserId",friendId);
        FriendsRequest friendsRequest = friendsRequestMapper.selectOneByExample(example);
        //查不到，表示可以添加，返回true。否则返回false；
        return friendsRequest == null;
    }

    //保存好友信息
    private void saveFriends(String senderId, String acceptUserId){
        MyFriends myFriends = new MyFriends();
        myFriends.setId(sid.nextShort());
        myFriends.setMyUserId(acceptUserId);
        myFriends.setMyFriendUserId(senderId);
        myFriendsMapper.insert(myFriends);
    }
}
