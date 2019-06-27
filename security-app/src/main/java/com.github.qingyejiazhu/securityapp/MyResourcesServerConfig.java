package com.github.qingyejiazhu.securityapp;

import com.github.qingyejiazhu.securityapp.social.openid.OpenIdAuthenticationSecurityConfig;
import com.github.qingyejiazhu.securitycore.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.github.qingyejiazhu.securitycore.authorize.AuthorizeConfigManager;
import com.github.qingyejiazhu.securitycore.properties.SecurityConstants;
import com.github.qingyejiazhu.securitycore.properties.SecurityProperties;
import com.github.qingyejiazhu.securitycore.validate.code.ValidateCodeSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableResourceServer
public class MyResourcesServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private SecurityProperties securityProperties;

    // 由下面的  .apply(smsCodeAuthenticationSecurityConfigs)方法添加这个配置
    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfigs;

    @Autowired
    private ValidateCodeSecurityConfig validateCodeSecurityConfig;
    /**
     * @see -SocialConfig #imoocSocialSecurityConfig()
     */
    @Autowired
    private SpringSocialConfigurer imoocSocialSecurityConfig;

    @Autowired
    private AuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private OpenIdAuthenticationSecurityConfig openIdAuthenticationSecurityConfig;

    @Autowired
    private AuthorizeConfigManager authorizeConfigManager;

    @Autowired
    private OAuth2WebSecurityExpressionHandler expressionHandler;
    // 有三个configure的方法，这里使用http参数的
    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 最简单的修改默认配置的方法
        // 在v5+中，该配置（表单登录）应该是默认配置了
        // basic登录（也就是弹框登录的）应该是v5-的版本默认
        // 这里copy了浏览器的安全配置 然后重构时处理重复代码
        http.formLogin()
                .loginPage(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL)
                .loginProcessingUrl(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM)
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(myAuthenticationFailureHandler)
        ;
        // 好奇这里的路径安全配置怎么没有了
        http
                // 这里验证表单和手机登录 中的 验证码
                .apply(validateCodeSecurityConfig)
                .and()
                .apply(smsCodeAuthenticationSecurityConfigs)
                .and()
                .apply(imoocSocialSecurityConfig)
                .and()
                .apply(openIdAuthenticationSecurityConfig)
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
                // 测试6.4 打开
                //.antMatchers("/user/me")
               // .access("hasAnyRole('ADMIN','USER','admin')")
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable();  // csrf 在后面章节会讲解;;
        authorizeConfigManager.config(http.authorizeRequests());
    }
    //oauth的bug（详见：https://github.com/spring-projects/spring-security-oauth/issues/730#issuecomment-219480394)，
    @Bean
    public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler(ApplicationContext applicationContext) {

        OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();

        expressionHandler.setApplicationContext(applicationContext);

        return expressionHandler;

    }

    @Override

    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {

        resources.expressionHandler(expressionHandler);

    }
}

