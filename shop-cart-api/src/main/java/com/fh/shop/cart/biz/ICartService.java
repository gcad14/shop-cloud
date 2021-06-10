package com.fh.shop.cart.biz;

import com.fh.shop.common.ServerResponse;

public interface ICartService {

    public ServerResponse addItem(Long member, Long skuId, Long count);

    ServerResponse findCart(Long memberid);

    ServerResponse findCartCount(Long memberid);

    ServerResponse deleteCartItem(Long memberid, Long skuId);

    ServerResponse deleteBatch(Long memberid, String ids);
}
