package com.ego.auth.controller;

import com.ego.auth.client.UserClient;
import com.ego.auth.entity.UserInfo;
import com.ego.auth.properties.JwtProperties;
import com.ego.auth.untils.JwtUtils;
import com.ego.common.enums.ExceptionEnum;
import com.ego.common.exception.EgoException;
import com.ego.common.util.CookieUtils;
import com.ego.user.pojo.User;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 **/
@Slf4j
@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private UserClient userClient;

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletResponse response,
            HttpServletRequest request
    ){
        //查询数据库获取登录用户数据
        try {
            User user = userClient.login(username, password).getBody();
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()), jwtProperties.getPrivateKey(), jwtProperties.getExpri());
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpri()*60);
            return ResponseEntity.ok().build();
        } catch (FeignException.BadRequest e){
            log.error("用户名密码错误",e);
            throw new EgoException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        } catch (Exception e) {
            log.error("创建jwt token错误",e);
            throw new EgoException(ExceptionEnum.JWT_CREATE_ERROR);
        }
    }
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("EGO_TOKEN")String token,HttpServletRequest request, HttpServletResponse response){
        try {
            // 从token中解析token信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
            // 解析成功要重新刷新token
            token = JwtUtils.generateToken(userInfo, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpri());
            // 更新cookie中的token
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpri()*60);
            // 解析成功返回用户信息
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 出现异常则，响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
