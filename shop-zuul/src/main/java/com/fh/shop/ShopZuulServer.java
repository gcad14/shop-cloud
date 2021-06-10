package com.fh.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ShopZuulServer {

    public static void main(String[] args) {
        SpringApplication.run(ShopZuulServer.class,args);
    }
}
