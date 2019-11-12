package com.wzd.gateway.filter;

import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class CommonFilter extends ZuulFilter {

    @Override
    public boolean shouldFilter() {

        return true;
    }

    @Override
    public int filterOrder() {

        return 0;
    }

    @Override
    public String filterType() {

        return "pre";
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext requestContext = RequestContext.getCurrentContext();
        requestContext.addZuulRequestHeader("member", "board");

        return null;
    }
}