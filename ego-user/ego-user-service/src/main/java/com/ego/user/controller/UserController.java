package com.ego.user.controller;

import com.ego.user.pojo.User;
import com.ego.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 *
 **/
//user/check/sdsd/1
@RestController
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 数据校验
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data") String data,@PathVariable(value = "type",required = false) Integer type){
        try {
            Boolean result = userService.checkData(data,type);
            if (result){
                return ResponseEntity.ok(true);
            }else {
                return ResponseEntity.badRequest().body(false);
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }

    }

    /**
     * 注册web
     * @param user
     * @param bs
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, BindingResult bs,@RequestParam("code") String code){
        try{
            //判断通过validation校验的user是否符合规则
            if (bs.hasErrors()){
                return ResponseEntity.badRequest().build();
            }
            Boolean result = userService.register(user,code);
            if (result){
                return ResponseEntity.ok().build();
            }else {
                return ResponseEntity.badRequest().build();
            }
        }catch (Exception e){
            System.out.println();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 发送手机验证码
     * @param phone
     * @return
     */
    @PostMapping("/send")
    public ResponseEntity<Void> sendVerifyCode(String phone) {
        Boolean boo = this.userService.sendVerifyCode(phone);
        if (boo == null || !boo) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 登录用户查询
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/login")
    ResponseEntity<User> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password){
        User user = userService.login(username, password);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(user);
    }
}


