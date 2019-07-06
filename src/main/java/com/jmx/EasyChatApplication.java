package com.jmx;

import com.jmx.utils.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.jmx.mapper")
//扫描需要的包
@ComponentScan(basePackages = {"com.jmx","org.n3r.idworker"})
public class EasyChatApplication {

    @Bean
    public SpringUtil springUtil(){
        return new SpringUtil();
    }

    public static void main(String[] args) {
        SpringApplication.run(EasyChatApplication.class);
    }
}
