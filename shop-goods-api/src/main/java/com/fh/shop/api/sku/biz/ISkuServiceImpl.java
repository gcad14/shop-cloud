package com.fh.shop.api.sku.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.goods.po.Sku;
import com.fh.shop.api.sku.mapper.ISkuMapper;
import com.fh.shop.api.sku.vo.SkuVo;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service("ISkuService")
@Transactional(rollbackFor = Exception.class)
public class ISkuServiceImpl implements ISkuService {
    @Resource
    private ISkuMapper iSkuMapper;

    @Override
    public ServerResponse findSkuList() {
        String skuList1 = RedisUtil.get("skuList");//获取redis中的数据
        if (StringUtils.isEmpty(skuList1)) {//如果redis中没有数据的情况
            List<Sku> sku =  iSkuMapper.findSku();
            List<SkuVo> collect = sku.stream().map(x -> {
                SkuVo skuVo = new SkuVo();
                skuVo.setId(x.getId());
                skuVo.setSkuName(x.getSkuName());
                skuVo.setImage(x.getImage());
                skuVo.setPrice(x.getPrice());
                skuVo.setStock(x.getStock());
                return skuVo;
            }).collect(Collectors.toList());
            String s = JSON.toJSONString(collect);
            RedisUtil.setex("skuList", s, 20);
            return ServerResponse.success(sku);
        } else {
            List<Sku> skuList = JSON.parseArray(skuList1, Sku.class);
            return ServerResponse.success(skuList);
        }
    }

    @Override
    public ServerResponse findSku(Long id) {
        Sku sku = iSkuMapper.selectById(id);
        return ServerResponse.success(sku);
    }


}
