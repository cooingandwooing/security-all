package com.github.qingyejiazhu.securitybrowser;

import com.github.qingyejiazhu.securitybrowser.authentication.MyAuthenticationSuccessHandler;
import com.github.qingyejiazhu.securitycore.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author gaoxiaofeng
 * @description
 * @date 2019-06-23 10:21
 */
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.formLogin()
                // 定义表单登录 - 身份认证的方式
                //.loginPage("/imooc-signIn.html")
                .loginPage("/authentication/require")
                // 处理登录请求路径
                .loginProcessingUrl("/authentication/form")
                .successHandler(myAuthenticationSuccessHandler)
//        http.httpBasic()
                .and()
                .authorizeRequests()
                // 别忘记了拦截放行的地方也需要更改为配置类的属性
                .antMatchers("/authentication/require", securityProperties.getBrowser().getLoginPage()).permitAll()
//                .antMatchers("/imooc-signIn.html", "/authentication/require").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable();  // csrf 在后面章节会讲解;

    }
}
