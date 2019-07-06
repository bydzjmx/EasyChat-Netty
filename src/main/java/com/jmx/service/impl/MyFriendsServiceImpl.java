package com.jmx.service.impl;

import com.jmx.mapper.MyFriendsMapper;
import com.jmx.service.MyFriendsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class MyFriendsServiceImpl implements MyFriendsService{

    @Resource
    private MyFriendsMapper myFriendsMapper;

}
