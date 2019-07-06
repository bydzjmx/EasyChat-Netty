package com.jmx.mapper;

import com.jmx.pojo.Users;
import com.jmx.utils.MyMapper;
import com.jmx.vo.FriendRequestVo;
import com.jmx.vo.MyFriendsVo;

import java.util.List;

/**
 * 用于接收添加好友者的信息
 */

public interface UsersMapperCustom extends MyMapper<Users> {

    List<FriendRequestVo> queryFriendRequestList(String acceptId);

    List<MyFriendsVo> queryMyFriends(String myUserId);

    void updateSignMsg(List<String> msgIdList);
}