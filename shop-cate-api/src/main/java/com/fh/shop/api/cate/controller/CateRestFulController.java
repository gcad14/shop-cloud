package com.fh.shop.api.cate.controller;


import com.fh.shop.api.cate.biz.ICateService;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
@Api(tags = "分类展示")
@Slf4j
public class CateRestFulController {

    @Resource(name = "ICateService")
    private ICateService iCateService;
//    @Value("${server.port}")
//    private Integer port;

    @ApiOperation("查询")
    @RequestMapping(value = "/cates", method = RequestMethod.GET)
    public ServerResponse cateList() {
//        log.info("端口号为：{}", port);
        return iCateService.cateList();
    }

}
