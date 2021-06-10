package com.fh.shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fh.shop.member.mapper")
public class ShopMemberApp {
    public static void main(String[] args) {
        SpringApplication.run(ShopMemberApp.class,args);
    }
}
