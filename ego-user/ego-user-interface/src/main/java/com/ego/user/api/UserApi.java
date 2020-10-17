package com.ego.user.api;

import com.ego.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 〈〉
 */
public interface UserApi {
    @GetMapping("/login")
    ResponseEntity<User> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password);
}
