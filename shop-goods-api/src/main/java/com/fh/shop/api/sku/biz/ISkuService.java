package com.fh.shop.api.sku.biz;

import com.fh.shop.common.ServerResponse;

public interface ISkuService {

    ServerResponse findSkuList();

    ServerResponse findSku(Long id);
}
