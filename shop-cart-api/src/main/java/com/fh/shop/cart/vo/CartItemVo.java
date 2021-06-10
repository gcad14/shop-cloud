package com.fh.shop.cart.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartItemVo implements Serializable {

    private String skuName;//名字
    private Long skuId;//id
    private String skuImage;//图片
    private String price;//价格
    private Long count;//个数
    private String subPrice;//小计
}
