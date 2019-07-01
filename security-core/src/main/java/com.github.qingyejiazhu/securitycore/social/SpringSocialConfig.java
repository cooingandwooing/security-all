/**
 *
 */
package com.github.qingyejiazhu.securitycore.social;

import com.github.qingyejiazhu.securitycore.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

/**
 * @author gaoxiaofeng
 * 开启并配置串联之前写的功能组件
 * SocialConfig spring包内有了 这里写成SpringSocialConfig
 */
@Configuration
@EnableSocial
public class SpringSocialConfig extends SocialConfigurerAdapter {
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private DataSource dataSource;

    /**
     * 不存在则不使用默认注册用户，而是跳转到注册页完成注册或则绑定
     */
    @Autowired(required = false)
    private ConnectionSignUp connectionSignUp;

    @Autowired(required = false)
    private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;

    /*
     *connectionFactoryLocator 查找我们配置的ConnectionFactory，我们有很多ConnectionFactory，有微信的和qq的
     *Encryptors 加解密，noOpText工具
     */
    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
        repository.setTablePrefix("zhanlu_");
        repository.setConnectionSignUp(connectionSignUp);
        return repository;
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    //兼容浏览器环境 和 app 环境
    @Bean
    public SpringSocialConfigurer mySocialSecurityConfig() {
        // 默认配置类，进行组件的组装
        // 包括了过滤器SocialAuthenticationFilter 添加到security过滤链中
        MySpringSocialConfigurer springSocialConfigurer = new MySpringSocialConfigurer();
        springSocialConfigurer.signupUrl(securityProperties.getBrowser().getSignUpUrl());
        // 设置后处理器 浏览器环境下不设后处理器，即为空；app环境下增加后处理器实现。
        springSocialConfigurer.setSocialAuthenticationFilterPostProcessor(socialAuthenticationFilterPostProcessor);
        return springSocialConfigurer;
    }

    /*
     // 只能处理浏览器环境
     @Bean
        public SpringSocialConfigurer imoocSocialSecurityConfig() {
            String filterProcessesUrl = securityProperties.getSocial().getFilterProcessesUrl();
            // 可配置的 路径
            ImoocSpringSocialConfigurer configurer = new ImoocSpringSocialConfigurer(filterProcessesUrl);
            configurer.signupUrl(securityProperties.getBrowser().getSignUpUrl());
            return configurer;
        }*/
    //https://docs.spring.io/spring-social/docs/1.1.x-SNAPSHOT/reference/htmlsingle/#creating-connections-with-connectcontroller
    // 必须要添加一个处理器
    // 后补：这个是提供查询社交账户信息服务，绑定服务，等
    @Bean
    public ConnectController connectController(
            ConnectionFactoryLocator connectionFactoryLocator,
            ConnectionRepository connectionRepository) {
        return new ConnectController(connectionFactoryLocator, connectionRepository);
    }

    /**
     *
     * @param connectionFactoryLocator
     * @param usersConnectionRepository 本类中上面定义的Override
     * @return
     */
    @Bean
    public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator,
                                                   UsersConnectionRepository usersConnectionRepository) {
       /*  ProviderSignInUtils 提供的工具类
         如果应用数据库中没有第三方用户关联 SocialAuthenticationProvider 抛出异常BadCredentialsException，
         SocialAuthenticationFilter 捕获异常catch (BadCredentialsException var5)，如果配置了跳转注册页 signupUrl，就在connection从token中拿出放入session中
        */
       return new ProviderSignInUtils(connectionFactoryLocator, usersConnectionRepository);
    }
}
