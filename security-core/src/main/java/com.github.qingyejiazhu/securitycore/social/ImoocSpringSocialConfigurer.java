/**
 * 
 */
package com.github.qingyejiazhu.securitycore.social;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * @author gaoxiaofeng
 * 在SpringSocialConfig产生imoocSocialSecurityConfig
 * // 浏览器环境下 spring 默认的处理器 但是在app环境下不行了
 */
public class ImoocSpringSocialConfigurer extends SpringSocialConfigurer {
	
	private String filterProcessesUrl;
	
	public ImoocSpringSocialConfigurer(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * J2SE 提供的最后一个批注是 @SuppressWarnings。该批注的作用是给编译器一条指令，告诉它对被批注的代码元素内部的某些警告保持静默。
	 * @SuppressWarnins("unchecked")
	 * 注掉了上行 不知道有实际用途没
	 * */
	@Override
	protected <T> T postProcess(T object) {
		// 浏览器环境下 spring 默认的处理器 但是在app环境下不行了
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		filter.setFilterProcessesUrl(filterProcessesUrl);
		return (T) filter;
	}

}
