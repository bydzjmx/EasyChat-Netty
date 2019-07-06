package com.jmx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SidTest {

    @Autowired
    private Sid sid;

    @Test
    public void test01(){
        String s = sid.nextShort();
        System.out.println(s);
    }

    @Test
    public void test03(){
        String words = ",101,102,103,";
        String[] split = words.split(",");
        for (String s : split) {
            System.out.println(s);
        }
    }

}
