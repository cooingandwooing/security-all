package com.github.qingyejiazhu.securitycore.social;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * @author : gaoxiaofeng
 * @version : V1.0
 * 在SpringSocialConfig产生imoocSocialSecurityConfig
 * // 浏览器环境下 spring 默认的处理器 可以处理app环境下  是ImoocSpringSocialConfigurer 的改进版
 * @date : 2018/8/6 12:12
 */
public class MySpringSocialConfigurer extends SpringSocialConfigurer {
    private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;
/*    private String filterProcessesUrl;

    public MySpringSocialConfigurer(String filterProcessesUrl) {
        this.filterProcessesUrl = filterProcessesUrl;
    }*/
    /**
     * J2SE 提供的最后一个批注是 @SuppressWarnings。该批注的作用是给编译器一条指令，告诉它对被批注的代码元素内部的某些警告保持静默。
     * @SuppressWarnins("unchecked")
     * 注掉了上行 不知道有实际用途没
     * */
    @Override
    protected <T> T postProcess(T object) {
        // org.springframework.security.config.annotation.SecurityConfigurerAdapter.postProcess()
        // 在SocialAuthenticationFilter中配置死的过滤器拦截地址
        // 这样的方法可以更改拦截的前缀
        SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
        // 默认/auth
       // filter.setFilterProcessesUrl(filterProcessesUrl);
//        filter.setFilterProcessesUrl("/oaths");
//        filter.setAuthenticationSuccessHandler();
        // 让使用处自己获取token成功的逻辑
        if (socialAuthenticationFilterPostProcessor != null) {
            socialAuthenticationFilterPostProcessor.process(filter);
        }
        return (T) filter;
    }

    public SocialAuthenticationFilterPostProcessor getSocialAuthenticationFilterPostProcessor() {
        return socialAuthenticationFilterPostProcessor;
    }

    public void setSocialAuthenticationFilterPostProcessor(SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor) {
        this.socialAuthenticationFilterPostProcessor = socialAuthenticationFilterPostProcessor;
    }
}
