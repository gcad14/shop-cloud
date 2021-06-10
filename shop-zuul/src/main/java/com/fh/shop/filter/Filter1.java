package com.fh.shop.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;


@Slf4j
public class Filter1 extends ZuulFilter {

    /**
     * pre  所有的客户端请求在访问真正的微服务之前执行
     * post  在访问微服务之后执行
     * route
     * error
     * @return
     */
    @Override
    public String filterType() {
//        return "pre";
        //上下两个相同
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 顺序   数值越小，优先级越高
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 当前过滤器是否生效
     * true 生效
     * false 不生效
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 业务逻辑处理
     * 永远返回 null
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        log.info("=========================================pre");
        return null;
    }
}
