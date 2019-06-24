package com.github.qingyejiazhu.securitybrowser;

import com.github.qingyejiazhu.securitybrowser.authentication.MyAuthenticationFailureHandler;
import com.github.qingyejiazhu.securitybrowser.authentication.MyAuthenticationSuccessHandler;
import com.github.qingyejiazhu.securitycore.properties.SecurityProperties;
import com.github.qingyejiazhu.securitycore.validate.code.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * @author gaoxiaofeng
 * @description
 * @date 2019-06-23 10:21
 */
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
    // 数据源是需要在使用处配置数据源的信息
    @Autowired
    private DataSource dataSource;
    @Autowired
    private PersistentTokenRepository persistentTokenRepository;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private SecurityProperties securityProperties;
    // 之前已经写好的 MyUserDetailsService
    @Autowired
    private UserDetailsService myUserDetailsService;
    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        // org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer.tokenRepository
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        // 该对象里面有定义创建表的语句
        // 可以设置让该类来创建表
        // 但是该功能只用使用一次，如果数据库已经存在表则会报错
//        jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }
    // 有三个configure的方法，这里使用http参数的
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        // 貌似自动注入了 validateCodeFilter.setFailureHandler(myAuthenticationFailureHandler);
        http
                // 由源码得知，在最前面的是UsernamePasswordAuthenticationFilter
                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                // 定义表单登录 - 身份认证的方式
                //.loginPage("/imooc-signIn.html")
                .loginPage("/authentication/require")
                // 处理登录请求路径
                .loginProcessingUrl("/authentication/form")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(myAuthenticationFailureHandler)
//        http.httpBasic()
                .and()
                // 从这里开始配置记住我的功能
                .rememberMe()
                .tokenRepository(persistentTokenRepository)
                // 新增过期配置，单位秒，默认配置写的60秒
                .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
//                .tokenValiditySeconds(60 * 5)
                .userDetailsService(myUserDetailsService)
                .and()
                .authorizeRequests()
                // 别忘记了拦截放行的地方也需要更改为配置类的属性
                .antMatchers("/authentication/require", securityProperties.getBrowser().getLoginPage(), "/code/image").permitAll()
//                .antMatchers("/imooc-signIn.html", "/authentication/require").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable();  // csrf 在后面章节会讲解;

    }
}
