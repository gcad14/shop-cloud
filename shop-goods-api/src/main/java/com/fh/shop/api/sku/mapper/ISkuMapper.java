package com.fh.shop.api.sku.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fh.shop.api.goods.po.Sku;

import java.util.List;

public interface ISkuMapper extends BaseMapper<Sku> {

    List<Sku> findSku();

//    List<SkuMailVo> findSkuList(int stockLimit);
//
//    int updateStock(CartItemVo cartItemVo);
//
//    void updateSkuStock(@Param("skuId") Long skuId, @Param("count") Long count);
//
//    void updateSale(@Param("skuCount") Long skuCount, @Param("skuId") Long skuId);
}
