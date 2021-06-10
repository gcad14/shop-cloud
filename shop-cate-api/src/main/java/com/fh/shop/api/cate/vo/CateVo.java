package com.fh.shop.api.cate.vo;

import com.fh.shop.api.cate.po.Cate;
import lombok.Data;

import java.io.Serializable;

@Data
public class CateVo implements Serializable {

    private Cate cate = new Cate();

//    private List<Type> typeList = new ArrayList<>();


}
