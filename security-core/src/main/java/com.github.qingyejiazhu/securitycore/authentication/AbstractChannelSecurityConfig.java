package com.github.qingyejiazhu.securitycore.authentication;

import com.github.qingyejiazhu.securitycore.properties.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 浏览器环境下安全配置主类
 * 貌似这个类十分重要
 * @see WebSecurityConfigurerAdapter# authenticationManager
 *
 * @author : zhuqiang
 * @version : V1.0
 * @date : 2018/8/5 16:06
 */
public class AbstractChannelSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler myAuthenticationFailureHandler;


    protected void applyPasswordAuthenticationConfig(HttpSecurity http) throws Exception {
        http.formLogin()
                // 定义表单登录 - 身份认证的方式
                //.loginPage("/imooc-signIn.html")
                .loginPage(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL)
                //这个是前台表单提交地址，让其知道这个请求用usernamepasswordauthenticatfilter 处理
                // 处理登录请求路径
                .loginProcessingUrl(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM)
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(myAuthenticationFailureHandler);
    }
}
