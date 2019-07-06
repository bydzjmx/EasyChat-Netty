package com.jmx;

import com.jmx.service.impl.UsersServiceImpl;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    private UsersServiceImpl usersService;

    @Test
    public void test01(){
        usersService.deleteFriendRequest("1907028CG90247F81","1907028BP34TDXGC1");
    }
}
