package com.github.qingyejiazhu.securityapp;

import com.github.qingyejiazhu.securitycore.properties.OAuth2ClientProperties;
import com.github.qingyejiazhu.securitycore.properties.SecurityProperties;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ${desc}
 *
 * @author zhuqiang
 * @version 1.0.1 2018/8/7 10:52
 * @date 2018/8/7 10:52
 * @since 1.0
 */
/*//AuthorizationServerConfigurerAdapter 可以添加自定义的配置 端点 安全性 第三方应用的配
// AuthorizationServerConfigurerAdapter这个类被继承后 默认的client_id 配置就不能用了 （待测试）,原因是OAuth2AuthorizationServerConfiguration 不会初始化 认证服务器不能正常工作
//用下面的配置*/
@Configuration
@EnableAuthorizationServer
public class MyAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final AuthenticationManager authenticationManager;

    @Autowired
    private SecurityProperties securityProperties;

    // 防止服务器重启  令牌失效
    /**
     * @see TokenStoreConfig
     * **/
    @Autowired(required = false)
    public TokenStore tokenStore;

    @Autowired(required = false)
    // 只有当使用jwt的时候才会有该对象
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    /**
     * @see TokenStoreConfig
     */
    @Autowired(required = false)
    private TokenEnhancer jwtTokenEnhancer;

    public MyAuthorizationServerConfig(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
    }

    // 给哪些应用发令牌
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 自己app和应用用的 内存的就好  如果像qq一样提供第三方认证 需要 数据库配
        InMemoryClientDetailsServiceBuilder builder = clients.inMemory();
        OAuth2ClientProperties[] clientsInCustom = securityProperties.getOauth2().getClients();
        if(ArrayUtils.isNotEmpty(clientsInCustom)){
            for (OAuth2ClientProperties p : clientsInCustom) {
                builder.withClient(p.getClientId())
                        .secret(p.getClientSecret())
                        .redirectUris(p.getRedirectUris())
                        .authorizedGrantTypes(p.getAuthorizedGrantTypes())
                        .accessTokenValiditySeconds(p.getAccessTokenValiditySeconds())
                        .refreshTokenValiditySeconds(2592000)
                        .scopes(p.getScopes());
            }
            logger.info(Arrays.toString(clientsInCustom));
        }
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(this.authenticationManager);
        endpoints.tokenStore(tokenStore);
        /**
         * 私有方法，但是在里面调用了accessTokenEnhancer.enhance所以这里使用链
         * @see DefaultTokenServices#createAccessToken(org.springframework.security.oauth2.provider.OAuth2Authentication, org.springframework.security.oauth2.common.OAuth2RefreshToken)
         */
        if (jwtAccessTokenConverter != null && jwtTokenEnhancer != null) {
            TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
            List<TokenEnhancer> enhancers = new ArrayList<>();
            enhancers.add(jwtTokenEnhancer);// 加信息的
            enhancers.add(jwtAccessTokenConverter);// 密签转换器
            enhancerChain.setTokenEnhancers(enhancers);
            // 一个处理链，先添加，再转换
            endpoints
                    .tokenEnhancer(enhancerChain)
                    .accessTokenConverter(jwtAccessTokenConverter);
        }
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 这里使用什么密码需要 根据上面配置client信息里面的密码类型决定
        // 目前上面配置的是无加密的密码
        security.passwordEncoder(NoOpPasswordEncoder.getInstance());
    }
}
