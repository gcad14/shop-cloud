package com.fh.shop.member.controller;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.BaseController;
import com.fh.shop.api.member.vo.MemberVo;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.member.biz.IMemberService;
import com.fh.shop.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


@RestController
@RequestMapping("/api")
@Api(tags = "会员接口")
public class MemberController extends BaseController {

    @Resource(name = "IMemberService")
    private IMemberService iMemberService;

    @Autowired
    private HttpServletRequest request;

//    @Autowired
//    private HttpServletResponse response;
//
//    @Value("${active.success}")
//    private String success;
//
//    @Value("${active.error}")
//    private String error;


    @GetMapping("/meber/login")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberName", value = "会员名", dataType = "java.lang.String", required = true),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "java.lang.String", required = true)
    })
    @ApiOperation(value = "会员登录")
    public ServerResponse login(String memberName, String password) {
        return iMemberService.login(memberName, password);
    }

    @GetMapping("/meber/logut")//注销退出登录
//    @Check
    public ServerResponse logut(){
        //将request中的信息费返回给前台
        MemberVo memberVo = buildMemberVo(request);
        RedisUtil.delete(KeyUtil.builMemberKey(memberVo.getId()));
        return ServerResponse.success(memberVo);
    }

    @GetMapping("/meber/findMember")//查看当前登录的会员
//    @Check
    @ApiImplicitParam(name = "x-auth", value = "头信息", paramType = "header", required = true)
    public ServerResponse findMember(){
        //将request中的信息费返回给前台
        MemberVo memberVo = buildMemberVo(request);
        return ServerResponse.success(memberVo);
    }

//    @GetMapping("/meber/memberInfo")
//    @ApiOperation("会员名查询")
//    public ServerResponse memberInfo(String memberName) {
//        return iMemberService.memberInfo(memberName);
//    }

    //会员激活
//    @GetMapping("/member/activeMember")
//    public void activeMember(String id) throws IOException {
//        int code = iMemberService.activeMamver(id);
//        if (code == Constants.REQUEST_ERROR) {//请求无效
//            response.sendRedirect(error);
//        } else {
//            response.sendRedirect(success);
//        }
//
//    }

//    @PostMapping("/member/addMember")
//    @ApiOperation(value = "会员注册")
//    public ServerResponse addMember(MemberParam memberParam) {
//        return iMemberService.addMember(memberParam);
//    }



//    @GetMapping("/meber")
//    public ServerResponse updateActive(String ngt) {
//        return iMemberService.updateActive(ngt);
//    }



//    @GetMapping("/meber/phoneInfo")
//    @ApiOperation("手机号查询")
//    public ServerResponse phoneInfo(String phone) {
//        return iMemberService.phoneInfo(phone);
//    }

//    @GetMapping("/meber/mailInfo")
//    @ApiOperation("邮箱查询")
//    public ServerResponse mailInfo(String mail) {
//        return iMemberService.mailInfo(mail);
//    }




//    @PostMapping("/meber/getMail")
//    @ApiOperation("找回密码")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "mail", value = "邮箱", dataType = "java.lang.String", required = true),
//            @ApiImplicitParam(name = "code", value = "验证码", dataType = "java.lang.String", required = true),
//            @ApiImplicitParam(name = "key", value = "图片key", dataType = "java.lang.String", required = true)
//    })
//    public ServerResponse getMail(String mail, String code, String key) {
//        return iMemberService.getMail(mail, code, key);
//    }

//    @PostMapping("/meber/updatePassword")
//    @ApiOperation("修改密码")
//    @Check
//    public ServerResponse updatePassword(MemberParam memberParam, HttpServletRequest request) {
//        return iMemberService.updatePassword(memberParam, request);
//    }


}
