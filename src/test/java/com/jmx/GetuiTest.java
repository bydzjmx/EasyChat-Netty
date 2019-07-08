package com.jmx;

import com.jmx.push.AppPush;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GetuiTest {

    @Test
    public void test01() throws Exception{
        AppPush.sendPush("好友请求","你收到新的好友添加请求","c7691ae51c2e8d475bcb899ae85e4697");
    }
}
