package com.github.qingyejiazhu.securitydemo.web.controller;

import com.github.qingyejiazhu.securitydemo.dto.User;
import com.github.qingyejiazhu.securitydemo.dto.UserQueryCondition;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoxiaofeng
 * @description
 * @date 2019/6/21 15:59
 */
@RestController
public class UserController {

    //这个是测试的 name 一致就不用写了
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    //public List<User> query(@RequestParam(required = false, name = "username", defaultValue = "no user")  String nikename){
    public List<User> query(UserQueryCondition condition, Pageable pageable) {

        System.out.println(ReflectionToStringBuilder.toString(condition, ToStringStyle.MULTI_LINE_STYLE));
        System.out.println(ReflectionToStringBuilder.toString(pageable, ToStringStyle.MULTI_LINE_STYLE));
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        users.add(new User());
        return users;
    }

    /*    @GetMapping("/me")
        public Authentication getCurrentUser() {
            // 认证结果如何在多个请求之间共享 SecurityContextHolder.getContext().setAuthentication(authResult); 自动注入的
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication;
        }
        // 这样写 spring 会注入
        @GetMapping("/me")
        public Authentication getCurrentUser(@AuthenticationPrincipal UserDetails user) {
            return user;
        }
        */
    // 这样写 spring 会注入
    @GetMapping("/me")
    public Authentication getCurrentUser(Authentication authentication) {
        return authentication;
    }
}
