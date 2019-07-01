/**
 * 
 */
package com.github.qingyejiazhu.securitycore.social;

import org.springframework.social.security.SocialAuthenticationFilter;

/**
 * @author gaoxiaofeng
 * 处理浏览器环境和app环境的不同
 *
 */
public interface SocialAuthenticationFilterPostProcessor {
	// 参数 当前的过滤器
	void process(SocialAuthenticationFilter socialAuthenticationFilter);

}
