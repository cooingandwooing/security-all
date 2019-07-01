package com.github.qingyejiazhu.securitydemo.web.controller;
import com.github.qingyejiazhu.securitycore.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.github.qingyejiazhu.securitycore.social.SignUpUtils;
import com.github.qingyejiazhu.securitydemo.dto.User;
import com.github.qingyejiazhu.securitydemo.dto.UserQueryCondition;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
//import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonView;
/**
 * @author gaoxiaofeng
 * @description
 * @date 2019/6/21 15:59
 */
@RestController
@RequestMapping("/user")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    //    @Autowired
//    private ProviderSignInUtils providerSignInUtils;
    @Autowired
    private SignUpUtils signUpUtils;
    @Autowired
    private SecurityProperties securityProperties;

    @GetMapping
    @JsonView(User.UserSimpleView.class)
    @ApiOperation(value = "用户查询服务")  // 方法描述
    public List<User> query(UserQueryCondition condition) {
        // compile group: 'org.apache.commons', name: 'commons-lang3', version: '3.7'
        // 一个反射工具类，这里把对象变成一个字符串，支持多种展示形式
        System.out.println(ReflectionToStringBuilder.toString(condition, ToStringStyle.MULTI_LINE_STYLE));
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        users.add(new User());
        return users;
    }
    // 获取用户详情
    // 在路径中使用参数
    @GetMapping("/{id:\\d+}")
    @JsonView(User.UserDetailView.class)
    public User getInfo(@ApiParam(value = "用户id") @PathVariable String id) {
        System.out.println("进入 getInfo 服务");
//        throw new UserNotExistException(id);
        User user = new User();
        user.setUsername("mrcode");
        return user;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user, BindingResult errors) {
        if (errors.hasErrors()) {
            errors.getAllErrors().stream().forEach(err -> {
                System.out.println(err.getDefaultMessage());
            });
        }
        System.out.println(ReflectionToStringBuilder.toString(user, ToStringStyle.MULTI_LINE_STYLE));
        user.setId("1");
        return user;
    }

    @PutMapping("/{id:\\d+}")
    public User update(@PathVariable() String id, @Valid @RequestBody User user, BindingResult errors) {
        if (errors.hasErrors()) {
            errors.getAllErrors().stream().forEach(err -> {
                FieldError fieldError = (FieldError) err;
                System.out.println(fieldError.getField() + " : " + err.getDefaultMessage());
            });
        }
        System.out.println(ReflectionToStringBuilder.toString(user, ToStringStyle.MULTI_LINE_STYLE));
        user.setId(id);
        return user;
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable String id) {
        System.out.println("id:" + id);
    }

    /**
     * 下面有几种获取方法，可以查看类里面的信息
     * @return
     */
    @GetMapping("/me")
    public Object getCurrentUser(@AuthenticationPrincipal UserDetails userDetails, Authentication authentication, HttpServletRequest request) throws UnsupportedEncodingException {
//        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        // Authorization : bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55IjoiaW1vb2MiLCJ1c2VyX25hbWUiOiJhZG1pbiIsImp0aSI6ImRjYzVmODIwLWUwNmYtNDYyNi1hYmMyLTAyZTljZjdkZjhmOCIsImNsaWVudF9pZCI6Im15aWQiLCJzY29wZSI6WyJhbGwiXX0.nYFBXcLBN3WNef0sooNxS0s6CaEleDGfjZh7xtTEqf4
        // 增加了jwt之后，获取传递过来的token
        // 当然这里只是其中一种的 token的传递方法，自己要根据具体情况分析
        String authorization = request.getHeader("Authorization");
        String token = StringUtils.substringAfter(authorization, "bearer ");
        logger.info("jwt token", token);
        String jwtSigningKey = securityProperties.getOauth2().getJwtSigningKey();
        // 生成的时候使用的是 org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
        // 源码里面把signingkey变成utf8了
        // JwtAccessTokenConverter类，解析出来是一个map
        // 所以这个自带的JwtAccessTokenConverter对象也是可以直接用来解析的
        byte[] bytes = jwtSigningKey.getBytes("utf-8");
        Claims claims = Jwts.parser().setSigningKey(bytes).parseClaimsJws(token).getBody();
        //String company = (String)claims.get("company");
        return claims;
    }
    //这个是测试的 name 一致就不用写了
/*    @RequestMapping(value = "/user", method = RequestMethod.GET)
    //public List<User> query(@RequestParam(required = false, name = "username", defaultValue = "no user")  String nikename){
    public List<User> query(UserQueryCondition condition, Pageable pageable) {

        System.out.println(ReflectionToStringBuilder.toString(condition, ToStringStyle.MULTI_LINE_STYLE));
        System.out.println(ReflectionToStringBuilder.toString(pageable, ToStringStyle.MULTI_LINE_STYLE));
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        users.add(new User());
        return users;
    }*/

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
/*    @GetMapping("/me")
    public Authentication getCurrentUser(Authentication authentication) {
        return authentication;
    }*/

    @PostMapping("/regist")
    public void regist(User user, HttpServletRequest request) {

        //不管是注册用户还是绑定用户，都会拿到一个用户唯一标识。
        String userId = user.getUsername();
        signUpUtils.doPostSignUp(userId, new ServletWebRequest(request));
    }
}
