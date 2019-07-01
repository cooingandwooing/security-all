/**
 *
 */
package com.github.qingyejiazhu.securityapp.social.impl;

import com.github.qingyejiazhu.securitycore.social.SocialAuthenticationFilterPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * @author gxf
 */
@Component
public class AppSocialAuthenticationFilterPostProcessor implements SocialAuthenticationFilterPostProcessor {

    /**
     * @see com.github.qingyejiazhu.securityapp.authentication.MyAuthenticationSuccessHandler
     * */
    @Autowired
    private AuthenticationSuccessHandler myAuthenticationSuccessHandler;

    /**
     * @see com.github.qingyejiazhu.securitycore.social.SocialAuthenticationFilterPostProcessor#process(SocialAuthenticationFilter)
     */
    @Override
    public void process(SocialAuthenticationFilter socialAuthenticationFilter) {
        // 将成功处理器设置成  返回令牌的
        socialAuthenticationFilter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
    }

}
