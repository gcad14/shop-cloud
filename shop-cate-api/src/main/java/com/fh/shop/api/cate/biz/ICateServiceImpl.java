package com.fh.shop.api.cate.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.cate.mapper.ICateMapper;
import com.fh.shop.api.cate.po.Cate;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("ICateService")
@Transactional(rollbackFor = Exception.class)
public class ICateServiceImpl implements ICateService {

    @Autowired
    private ICateMapper iCateMapper;

    @Override
    public ServerResponse cateList() {
        //获取redis数据库中的数据
        String cateList = RedisUtil.get("cateList");
        if (StringUtils.isEmpty(cateList)) {
            List<Cate> cates = iCateMapper.selectList(null);
            String cateJSON = JSON.toJSONString(cates);
            RedisUtil.set("cateList", cateJSON);
            return ServerResponse.success(cates);
        } else {
            List<Cate> cates = JSON.parseArray(cateList, Cate.class);
            return ServerResponse.success(cates);
        }
        //如果redis为空则从数据库中查询数据
    }

}
