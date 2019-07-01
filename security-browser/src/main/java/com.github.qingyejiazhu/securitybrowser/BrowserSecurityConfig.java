package com.github.qingyejiazhu.securitybrowser;

import com.github.qingyejiazhu.securitybrowser.authentication.MyAuthenticationFailureHandler;
import com.github.qingyejiazhu.securitybrowser.authentication.MyAuthenticationSuccessHandler;
import com.github.qingyejiazhu.securitycore.authentication.AbstractChannelSecurityConfig;
import com.github.qingyejiazhu.securitycore.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.github.qingyejiazhu.securitycore.authorize.AuthorizeConfigManager;
import com.github.qingyejiazhu.securitycore.properties.SecurityConstants;
import com.github.qingyejiazhu.securitycore.properties.SecurityProperties;
import com.github.qingyejiazhu.securitycore.properties.SessionProperties;
import com.github.qingyejiazhu.securitycore.social.SpringSocialConfig;
import com.github.qingyejiazhu.securitycore.validate.code.ValidateCodeSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

/**
 * @author gaoxiaofeng
 * @description
 * @date 2019-06-23 10:21
 */
@Configuration
public class BrowserSecurityConfig extends AbstractChannelSecurityConfig {
    // 数据源是需要在使用处配置数据源的信息
    @Autowired
    private DataSource dataSource;
    @Autowired
    private PersistentTokenRepository persistentTokenRepository;

    @Autowired
    private ValidateCodeSecurityConfig validateCodeSecurityConfig;
    // 由下面的  .apply(smsCodeAuthenticationSecurityConfigs)方法添加这个配置
    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfigs;
    @Autowired
    private SecurityProperties securityProperties;

    // 之前已经写好的 MyUserDetailsService
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    /**
     * @see SpringSocialConfig#mySocialSecurityConfig() 继承之后修改MySpringSocialConfigurer postProcess
     */
    @Autowired
    private SpringSocialConfigurer mySocialSecurityConfig;

    /**
     * @see BrowserSecurityBeanConfig
     */
    @Autowired
    private InvalidSessionStrategy invalidSessionStrategy;
    /**
     * @see BrowserSecurityBeanConfig
     */
    @Autowired
    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private AuthorizeConfigManager authorizeConfigManager;

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
        applyPasswordAuthenticationConfig(http);
        SessionProperties session = securityProperties.getBrowser().getSession();

        // 一条搞定
        http.apply(validateCodeSecurityConfig)
                    .and()
                .apply(smsCodeAuthenticationSecurityConfigs)
                    .and()
                .apply(mySocialSecurityConfig) // 加一个过滤器 拦截特定请求做社交登陆
                    .and()
                // 从这里开始配置记住我的功能
                .rememberMe()
                    .tokenRepository(persistentTokenRepository)
                    // 新增过期配置，单位秒，默认配置写的60秒
                    .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
    //                .tokenValiditySeconds(60 * 5)
                    .userDetailsService(userDetailsService)
                    .and()
                .sessionManagement()
                    .invalidSessionStrategy(invalidSessionStrategy) //默认/session/invalid 可配置
                    .maximumSessions(session.getMaximumSessions()) //限制同一个用户只能有一个session登录
                    .maxSessionsPreventsLogin(session.isMaxSessionsPreventsLogin())  // 当session达到最大后，阻止后登录的行为
                    .expiredSessionStrategy(sessionInformationExpiredStrategy)  // 失效后的策略。定制型更高，失效前的请求还能拿到
                    .and()
                    .and()
                .logout()
//                   .logoutUrl("/singout")  // 退出请求路径
                    // 与logoutSuccessUrl互斥，有handler则logoutSuccessUrl失效
                    // 通过处理器增加配置了页面则跳转到页面，没有则输出json
                    .logoutSuccessHandler(logoutSuccessHandler)
                    .deleteCookies("JSESSIONID")
                    .and()
                .authorizeRequests()
                // 别忘记了拦截放行的地方也需要更改为配置类的属性
                .antMatchers(
                        SecurityConstants.DEFAULT_UNAUTHENTICATION_URL,
                        SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE,
                        securityProperties.getBrowser().getLoginPage(),
                        SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX+"/*",
                        securityProperties.getBrowser().getSignUpUrl(),
                        securityProperties.getBrowser().getSession().getSessionInvalidUrl()+".json",
                        securityProperties.getBrowser().getSession().getSessionInvalidUrl()+".html",
                        "/user/regist","/session/invalid")// 这个是用户知道的注册路径 暂且写到这里
                .permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                .csrf().disable();  // csrf 在后面章节会讲解;

    }
}
