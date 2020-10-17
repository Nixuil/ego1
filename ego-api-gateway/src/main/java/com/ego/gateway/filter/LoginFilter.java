package com.ego.gateway.filter;

import com.ego.auth.untils.JwtUtils;
import com.ego.common.util.CookieUtils;
import com.ego.properties.JwtProperties;
import com.ego.properties.PathProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 *
 **/
@Slf4j
@Component
@EnableConfigurationProperties({JwtProperties.class, PathProperties.class})
public class LoginFilter extends ZuulFilter {

    @Resource
    private PathProperties pathProperties;
    @Resource
    private JwtProperties jwtProperties;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        String uri = RequestContext.getCurrentContext().getRequest().getRequestURI();
        boolean match = pathProperties.getAllowPaths().stream().anyMatch(path -> uri.startsWith(path));
        return !match;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        try {
            JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
        } catch (Exception e) {
            log.error("token无效:",token);
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
        }
        return null;
    }
}
