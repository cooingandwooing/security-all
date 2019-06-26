package com.github.qingyejiazhu.securitycore.social.qq.config;

import com.github.qingyejiazhu.securitycore.properties.QQProperties;
import com.github.qingyejiazhu.securitycore.properties.SecurityProperties;
import com.github.qingyejiazhu.securitycore.social.qq.connet.QQOAuth2ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;

/**
 * autoconfigure2.04中已经不存在social的自动配置类了
 * org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter
 * @author : zhuqiang
 * @version : V1.0
 * @date : 2018/8/6 9:20
 */
@Configuration
// 当配置了app-id的时候才启用，qq的连接工程，只有这个配置文件配置，下面才起作用
@ConditionalOnProperty(prefix = "zhanlu.security.social.qq", name = "app-id")
public class QQAutoConfig extends SocialConfigurerAdapter {
    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer configurer,
                                       Environment environment) {
        configurer.addConnectionFactory(createConnectionFactory());
    }

    public ConnectionFactory<?> createConnectionFactory() {
        QQProperties qq = securityProperties.getSocial().getQq();
        return new QQOAuth2ConnectionFactory(qq.getProviderId(), qq.getAppId(), qq.getAppSecret());
    }

    // 后补：做到处理注册逻辑的时候发现的一个bug：登录完成后，数据库没有数据，但是再次登录却不用注册了
    // 就怀疑是否是在内存中存储了。结果果然发现这里父类的内存ConnectionRepository覆盖了SocialConfig中配置的jdbcConnectionRepository
    // 这里需要返回null，否则会返回内存的 ConnectionRepository
    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return null;
    }
}
