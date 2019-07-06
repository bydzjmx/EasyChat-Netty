package com.jmx.service.impl;

import com.jmx.mapper.FriendsRequestMapper;
import com.jmx.service.FriendsRequestService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class FriendsRequestServiceImpl implements FriendsRequestService{

    @Resource
    private FriendsRequestMapper friendsRequestMapper;

}
