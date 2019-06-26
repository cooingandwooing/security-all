/**
 * 
 */
package com.github.qingyejiazhu.securitycore.social.weixin.config;

import com.github.qingyejiazhu.securitycore.properties.SecurityProperties;
import com.github.qingyejiazhu.securitycore.properties.WeixinProperties;
import com.github.qingyejiazhu.securitycore.social.MyConnectView;
import com.github.qingyejiazhu.securitycore.social.weixin.connect.WeixinConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.web.servlet.View;


/**
 * 微信登录配置
 * 
 * @author zhailiang
 *
 */
@Configuration
@ConditionalOnProperty(prefix = "zhanlu.security.social.weixin", name = "app-id")
public class WeixinAutoConfiguration extends SocialConfigurerAdapter {

	@Autowired
	private SecurityProperties securityProperties;

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer configurer,
									   Environment environment) {
		configurer.addConnectionFactory(createConnectionFactory());
	}
/*	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
		super.addConnectionFactories(connectionFactoryConfigurer, environment);
		XProperties xProperties = SysProperties.getSocial().getX();
		YProperties yProperties = SysProperties.getSocial().getY();
		connectionFactoryConfigurer.addConnectionFactory(
				new XConnectionFactory(xProperties .getProviderId(), xProperties .getAppId(), xProperties .getAppSecret()));
		connectionFactoryConfigurer.addConnectionFactory(
				new YConnectionFactory(yProperties .getProviderId(), yProperties .getAppId(), yProperties .getAppSecret()));
	}*/

	protected ConnectionFactory<?> createConnectionFactory() {
		WeixinProperties weixinConfig = securityProperties.getSocial().getWeixin();
		return new WeixinConnectionFactory(weixinConfig.getProviderId(), weixinConfig.getAppId(),
				weixinConfig.getAppSecret());
	}
	// 这里需要返回null，否则会返回内存的 ConnectionRepository
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return null;
	}
	// 这里是解绑用的 和 绑定 才做成功的 视图一样 判断 model里面有connection
	@Bean({"connect/weixinConnect", "connect/weixinConnected"})
	@ConditionalOnMissingBean(name = "weixinConnectedView")
	public View weixinConnectedView() {
		return new MyConnectView();
	}
	
}
