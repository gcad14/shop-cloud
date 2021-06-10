package com.fh.shop.cart.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.goods.IGoodsFeignService;
import com.fh.shop.api.goods.po.Sku;
import com.fh.shop.cart.vo.CartItemVo;
import com.fh.shop.cart.vo.CartVo;
import com.fh.shop.common.Constants;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.BigDecimalUtil;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service("cartService")
@Transactional(rollbackFor = Exception.class)
public class ICartServiceImpl implements ICartService {

    @Resource
    private IGoodsFeignService goodsFeignService;
    @Value("${sku.count.limit}")
    private int countLimit;

    @Override
    public ServerResponse addItem(Long memberid, Long skuId, Long count) {
        //设置最大购买数量
        if (count > countLimit) {
            return ServerResponse.error(ResponseEnum.CART_SKU_COUNT_LIMIT);//商品限额了
        }
        //商品是否存在
        ServerResponse<Sku> sku1 = goodsFeignService.findSku(skuId);
        Sku sku =sku1.getData();// iSkuMapper.selectById(skuId);
        if (sku == null) {
            return ServerResponse.error(ResponseEnum.CART_SKU_IS_NULL);//商品不存在
        }
        //商品是否上架
        if (sku.getIsup().equals(Constants.STATUS_DOWN)) {
            return ServerResponse.error(ResponseEnum.CART_SKU_IS_DOWN);//商品已下架
        }
        //商品的库存是否大于或等于用户想要购买的数量
        int stock = sku.getStock();
        if (stock < count) {
            return ServerResponse.error(ResponseEnum.CART_SKU_STOCK_IS_ERROR);//库存不足
        }
        //判断会员是否有购物车
        String key = KeyUtil.buildCartKey(memberid);
//        String cartJson = RedisUtil.get(key);
        Boolean exist = RedisUtil.exist(key);
        //判断是否有购物车
        if (!exist) {//没有购物车，创建一个购物车，把商品加入到购物车
            if (count < 0) {
                return ServerResponse.error(ResponseEnum.CART_IS_ERROR);
            }
            CartVo cartVo = new CartVo();
            CartItemVo cartItemVo = new CartItemVo();
            cartItemVo.setCount(count);//添加的个数
            String price = sku.getPrice().toString();
            cartItemVo.setPrice(price);//单价
            cartItemVo.setSkuId(sku.getId());//skuid
            cartItemVo.setSkuImage(sku.getImage());//图片
            cartItemVo.setSkuName(sku.getSkuName());//名称
            BigDecimal subPrice = BigDecimalUtil.mul(price, count + "");
            cartItemVo.setSubPrice(subPrice.toString());//小计
            cartVo.getCartItemVoList().add(cartItemVo);
            cartVo.setTotalCount(count);//总数
            cartVo.setTotalPrice(cartItemVo.getSubPrice());//总价格
            //更新购物车
//            RedisUtil.set(key, JSON.toJSONString(cartVo));
            RedisUtil.hset(key, Constants.CART_JSON_FIELD, JSON.toJSONString(cartVo));//存入购物车数据
            RedisUtil.hset(key, Constants.CART_COUNT_FIELD, cartVo.getTotalCount() + "");//存入购物车总条数
        } else {//有购物车
            String cartJson = RedisUtil.hget(key, Constants.CART_JSON_FIELD);
            CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
            List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
            Optional<CartItemVo> item = cartItemVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
            if (item.isPresent()) {//购物车中有商品
                CartItemVo cartItemVo = item.get();
                Long limitCount = cartItemVo.getCount() + count;
                //购买数量限额
                if (limitCount > countLimit) {
                    return ServerResponse.error(ResponseEnum.CART_SKU_COUNT_LIMIT);//商品限额了
                }
                //
                if (limitCount <= 0) {
                    //删除当前商品
                    cartItemVoList.removeIf(x -> x.getSkuId().longValue() == cartItemVo.getSkuId().longValue());
                    if (cartItemVoList.size() == 0) {
                        //删除购物车
                        RedisUtil.delete(key);
                        return ServerResponse.success();
                    }
                    //更新购物车
                    updateCart(cartVo, key);
                    return ServerResponse.success();
                }
                cartItemVo.setCount(limitCount);//设置购买数量
                BigDecimal subPrice = new BigDecimal(cartItemVo.getSubPrice());//获取当前商品的价格
                String subPriceStr = subPrice.add(BigDecimalUtil.mul(cartItemVo.getPrice(), count + "")).toString();//获取小计
                cartItemVo.setSubPrice(subPriceStr);//设置小计
                //更新购物车
                updateCart(cartVo, key);
            } else {//购物车中没有此商品，直接加入购物车
                if (count < 0) {
                    return ServerResponse.error(ResponseEnum.CART_IS_ERROR);
                }
                CartItemVo cartItemVo = new CartItemVo();
                cartItemVo.setCount(count);//添加的个数
                String price = sku.getPrice().toString();
                cartItemVo.setPrice(price);//单价
                cartItemVo.setSkuId(sku.getId());//skuid
                cartItemVo.setSkuImage(sku.getImage());//图片
                cartItemVo.setSkuName(sku.getSkuName());//名称
                BigDecimal subPrice = BigDecimalUtil.mul(price, count + "");
                cartItemVo.setSubPrice(subPrice.toString());//小计
                cartVo.getCartItemVoList().add(cartItemVo);
                //更新购物车
                updateCart(cartVo, key);
            }
        }
        return ServerResponse.success();
    }

    @Override
    public ServerResponse findCart(Long memberid) {
        String cartJson = RedisUtil.hget(KeyUtil.buildCartKey(memberid), Constants.CART_JSON_FIELD);
        CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
        return ServerResponse.success(cartVo);
    }

    @Override
    public ServerResponse findCartCount(Long memberid) {
        String count = RedisUtil.hget(KeyUtil.buildCartKey(memberid), Constants.CART_COUNT_FIELD);
        return ServerResponse.success(count);
    }

    @Override
    public ServerResponse deleteCartItem(Long memberid, Long skuId) {
        //获取会员对应的购物车
        String key = KeyUtil.buildCartKey(memberid);
        String cartJson = RedisUtil.hget(key, Constants.CART_JSON_FIELD);
        CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        Optional<CartItemVo> itemVo = cartItemVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
        if (!itemVo.isPresent()) {
            return ServerResponse.error(ResponseEnum.CART_IS_ERROR);
        }
        cartItemVoList.removeIf(x -> x.getSkuId().longValue() == skuId.longValue());
        if (cartItemVoList.size() == 0) {//如果购物车中没有数据则删除购物车
            RedisUtil.delete(key);
            return ServerResponse.success();
        }
        //更新购物车
        updateCart(cartVo, key);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse deleteBatch(Long memberid, String ids) {
        if (StringUtils.isEmpty(ids)) {
            return ServerResponse.error(ResponseEnum.CART_BATCH_DELETE_NO_SELECT);
        }
        //获取会员对应的购物车
        String key = KeyUtil.buildCartKey(memberid);
        String cartJson = RedisUtil.hget(key, Constants.CART_JSON_FIELD);
        CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        Arrays.stream(ids.split(",")).forEach(x -> cartItemVoList.removeIf(y -> y.getSkuId().longValue() == Long.parseLong(x)));
        if (cartItemVoList.size() == 0) {//如果购物车中没有数据则删除购物车
            RedisUtil.delete(key);
            return ServerResponse.success();
        }
        //更新购物车
        updateCart(cartVo, key);
        return ServerResponse.success();
    }

    public static void updateCart(CartVo cartVo, String key) {
        List<CartItemVo> cartItemVos = cartVo.getCartItemVoList();
        long totalCount = 0;//总数
        BigDecimal totalPrice = new BigDecimal(0);//总价格
        for (CartItemVo itemVo : cartItemVos) {
            totalCount += itemVo.getCount();//总数
            totalPrice = totalPrice.add(new BigDecimal(itemVo.getSubPrice()));//总价格
        }
        cartVo.setTotalCount(totalCount);//总数
        cartVo.setTotalPrice(totalPrice.toString());//总价格
        //更新购物车
        RedisUtil.hset(key, Constants.CART_JSON_FIELD, JSON.toJSONString(cartVo));//存入购物车数据
        RedisUtil.hset(key, Constants.CART_COUNT_FIELD, cartVo.getTotalCount() + "");//存入购物车总条数
    }

}
