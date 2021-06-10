package com.fh.shop.cart.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CartVo implements Serializable {
    private List<CartItemVo> cartItemVoList = new ArrayList<>();
    private Long totalCount;//总数量
    private String totalPrice;//总价格
}
