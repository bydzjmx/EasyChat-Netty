package com.jmx.service;

import com.jmx.netty.ChatData;
import com.jmx.pojo.ChatMsg;
import com.jmx.pojo.Users;
import com.jmx.vo.FriendRequestVo;
import com.jmx.vo.MyFriendsVo;

import java.util.List;

public interface UsersService{

    /**
     * 查询用户名是否存在
     * @param username
     * @return
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 用户登录校验
     * @param username
     * @param pwd
     * @return
     */
    Users queryUserForLogin(String username, String pwd);

    /**
     * 用户注册接口
     * @param user
     * @return
     */
    Users saveUser(Users user);

    /**
     * 更新用户信息
     * @param users
     * @return
     */
    Users updateUsersInfo(Users users);

    /**
     * 搜索朋友的前置条件查询
     * @param myUserId
     * @param username
     * @return
     */
    Integer preSearchFriends(String myUserId, String username);

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    Users  queryUserByUsername(String username);

    /**
     * 发送添加好友请求
     * @param myUserId
     * @param username
     */
    void sendAddFriendRequest(String myUserId, String username);

    /**
     * 查询想添加我的人的信息
     * @param acceptId
     * @return
     */
    List<FriendRequestVo> queryFriendRequestList(String acceptId);

    /**
     * 删除好友请求表,忽略好友添加请求
     * @param senderId 发送请求的id
     * @param acceptUserId 接收请求的id
     */
    void deleteFriendRequest(String senderId, String acceptUserId);

    /**
     * 通过好友添加请求
     * @param senderId 发送请求的id
     * @param acceptUserId 接收请求的id
     */
    void passFriendRequest(String senderId, String acceptUserId);

    /**
     * 查询我的所有好友
     * @param userId
     */
    List<MyFriendsVo> queryMyFriends(String userId);

    /**
     * 保存消息到数据库中,返回msgId
     * @param chatData
     * @return
     */
    String saveMsg(ChatData chatData);

    /**
     * 批量签收消息状态
     * @param msgIdList
     */
    void updateSignMsg(List<String> msgIdList);

    List<ChatMsg> queryUnReadMsg(String acceptUserId);
}
