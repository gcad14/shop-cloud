package com.fh.shop.cart.controller;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.BaseController;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.cart.biz.ICartService;
import com.fh.shop.common.Constants;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping("/api/cart")
@Api(tags = "购物车接口")
public class CartController extends BaseController {

    @Autowired
    private HttpServletRequest request;

    @Resource(name = "cartService")
    private ICartService cartService;

    //加入购物车
//    @Check
    @PostMapping("/addCartItrm")
    @ApiOperation("添加商品到购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId", value = "商品id", dataType = "java.lang.long", required = true),
            @ApiImplicitParam(name = "count", value = "商品数量", dataType = "java.lang.long", required = true),
            @ApiImplicitParam(name = "x-auth", value = "头部信息", dataType = "java.lang.String", required = true, paramType = "header")
    })
    private ServerResponse addCartItrm(Long skuId, Long count) {
//        MemberVo membervo = (MemberVo) request.getAttribute(Constants.SECRET);

//        String memberVoJson = URLDecoder.decode(request.getHeader(Constants.SECRET),"UTF-8");//获取参数的时候解密
//        MemberVo memberVo = JSON.parseObject(memberVoJson, MemberVo.class);
        MemberVo memberVo = buildMemberVo(request);
        Long memberid = memberVo.getId();
        return cartService.addItem(memberid, skuId, count);
    }

//    @Check
    @GetMapping("/findCart")
    @ApiOperation("查询购物车中的商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth", value = "头部信息", dataType = "java.lang.String", required = true, paramType = "header")
    })
    public ServerResponse findCart() {
        MemberVo memberVo = buildMemberVo(request);
        Long memberid = memberVo.getId();
        return cartService.findCart(memberid);
    }

//    @Check
    @GetMapping("/findCartCount")
    @ApiOperation("查询购物车中的商品数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth", value = "头部信息", dataType = "java.lang.String", required = true, paramType = "header")
    })
    public ServerResponse findCartCount() {
        MemberVo memberVo = buildMemberVo(request);
        Long memberid = memberVo.getId();
        return cartService.findCartCount(memberid);
    }

//    @Check
    @DeleteMapping("/deleteCartItem")
    @ApiOperation("删除购物车中的商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth", value = "头部信息", dataType = "java.Lang.String", required = true, paramType = "header")
    })
    public ServerResponse deleteCartItem(Long skuId) {
        //获取当前会员信息
        MemberVo memberVo = buildMemberVo(request);
        Long memberid = memberVo.getId();
        return cartService.deleteCartItem(memberid, skuId);
    }

//    @Check
    @DeleteMapping("/deleteBatch")
    @ApiOperation("批量删除购物车中的商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth", value = "头部信息", dataType = "java.Lang.String", required = true, paramType = "header")
    })
    public ServerResponse deleteBatch(@PathParam("ids") String ids) {
        //获取当前会员信息
        MemberVo memberVo = buildMemberVo(request);
        Long memberid = memberVo.getId();
        return cartService.deleteBatch(memberid, ids);
    }
}
