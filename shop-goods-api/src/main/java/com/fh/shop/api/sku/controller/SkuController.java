package com.fh.shop.api.sku.controller;

import com.fh.shop.api.sku.biz.ISkuService;
import com.fh.shop.common.ServerResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
public class SkuController {

    @Resource(name = "ISkuService")
    private ISkuService iSkuService;

    @GetMapping("/skus")
    private ServerResponse findSkuList() {
        return iSkuService.findSkuList();
    }

    @GetMapping("/skus/findSku")
    public ServerResponse findSku(@RequestParam("id") Long id){
            return iSkuService.findSku(id);
    }

}
