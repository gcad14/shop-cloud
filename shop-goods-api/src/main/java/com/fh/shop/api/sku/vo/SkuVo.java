package com.fh.shop.api.sku.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SkuVo implements Serializable {

    private Long id;

    private String skuName;

    private BigDecimal price;

    private String image;

    private int stock;


}
