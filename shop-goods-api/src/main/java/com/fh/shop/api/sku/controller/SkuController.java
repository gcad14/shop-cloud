package com.fh.shop.api.sku.controller;

import com.fh.shop.api.sku.biz.ISkuService;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
@Api(tags = "商品接口")
public class SkuController {

    @Resource(name = "ISkuService")
    private ISkuService iSkuService;

    @GetMapping("/skus")
    @ApiOperation("查询商品的信息")
    private ServerResponse findSkuList() {
        return iSkuService.findSkuList();
    }

    @GetMapping("/skus/findSku")
    public ServerResponse findSku(@RequestParam("id") Long id){
            return iSkuService.findSku(id);
    }

}
