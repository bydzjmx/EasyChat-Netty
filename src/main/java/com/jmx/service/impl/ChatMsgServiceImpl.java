package com.jmx.service.impl;

import com.jmx.mapper.ChatMsgMapper;
import com.jmx.service.ChatMsgService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class ChatMsgServiceImpl implements ChatMsgService{

    @Resource
    private ChatMsgMapper chatMsgMapper;

}
