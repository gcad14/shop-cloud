package com.fh.shop.api.cate.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Cate implements Serializable {

    private Long id;

    private String cateName;

    private Long typeId;

    private Long fatherId;

    private String typeName;


}
