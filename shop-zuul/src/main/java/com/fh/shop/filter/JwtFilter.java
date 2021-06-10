package com.fh.shop.filter;

import com.alibaba.fastjson.JSON;
import com.fh.shop.common.Constants;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import com.fh.shop.vo.MemberVo;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;

@Component
public class JwtFilter extends ZuulFilter {

    @Value("${fh.shop.checkUrls}")
    private List<String> checkUrls;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @SneakyThrows
    @Override
    public Object run() throws ZuulException {
        //获取当前访问的路径
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        boolean isCheck = false;
        for (String checkUrl : checkUrls) {
            if (requestURL.indexOf(checkUrl) > 0) {
                isCheck = true;
                break;
            }
        }
        if (!isCheck) {
            return null;//放行   证明没有需要被拦截的请求
        }
        //进行验证
        //判断请求头是否为空【eyJpZCI6MSwibmlja05hbWUiOiJycnIifQ==.RFE0NkE1RDRRRTQxMjU2QUQ=】
        //自定义头像信息
        String header = request.getHeader("x-auth");
        if (StringUtils.isEmpty(header)) {
            return buildResponse(ResponseEnum.TOKEN_HEADLE_NULL);
        }
//        //判断头部信息是否完整
        String[] headerArr = header.split("\\.");
        if (headerArr.length != 2) {
            return buildResponse(ResponseEnum.TOKEN_ERROR);
        }
//        //验签
//        //用户信息
        String member = headerArr[0];
//        //签名
        String signs = headerArr[1];
//        //解码
        String memberVoJson = new String(Base64.getDecoder().decode(member), "utf-8");
        String signDecoder = new String(Base64.getDecoder().decode(signs), "utf-8");
        String newSign = Md5Util.sign(memberVoJson, Constants.SECRET);
        if (!newSign.equals(signDecoder)) {
            return buildResponse(ResponseEnum.MEMEBER_SECRET_ERROR);
            //不相等是给前台一个提示
//            throw new ShopException(ResponseEnum.MEMEBER_SECRET_ERROR);
        }
        MemberVo memberVo = JSON.parseObject(memberVoJson, MemberVo.class);
        Long id = memberVo.getId();


        //设置JWT的过期时间
        if (!RedisUtil.exist(KeyUtil.builMemberKey(id))) {
//            throw new ShopException(ResponseEnum.SECRET_ERROR);
            return buildResponse(ResponseEnum.SECRET_ERROR);
        }
        //重新刷新登录时间
        RedisUtil.expire(KeyUtil.builMemberKey(id), Constants.TOKEN_EXPIRE);
        //将登录后的用户信息放入到request请求中
//        request.setAttribute(Constants.SECRET, memberVo);//由于是为服务所以request已经不再是之前的request了
        //将要传给后台微服务的信息放到请求头中
        currentContext.addZuulRequestHeader(Constants.SECRET,URLEncoder.encode(memberVoJson,"UTF-8"));//传递参数的时候进行编码
        return null;
    }

    public Object buildResponse(ResponseEnum responseEnum) {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        response.setContentType("application/json:charset=UTF-8");//解决中文乱码问题
        //先前台返回一个提示信息
        currentContext.setSendZuulResponse(false);//进行拦截，不会再路由转发
        //提示json信息
        ServerResponse error = ServerResponse.error(responseEnum);
        String res = JSON.toJSONString(error);
        currentContext.setResponseBody(res);
        //返回null
        return null;
    }
}
