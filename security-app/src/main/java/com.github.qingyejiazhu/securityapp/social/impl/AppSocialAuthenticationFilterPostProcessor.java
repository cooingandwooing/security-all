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
 * @author zhailiang
 */
@Component
public class AppSocialAuthenticationFilterPostProcessor implements SocialAuthenticationFilterPostProcessor {

    @Autowired
    private AuthenticationSuccessHandler imoocAuthenticationSuccessHandler;

    /**
     * @see com.github.qingyejiazhu.securitycore.social.SocialAuthenticationFilterPostProcessor#process(SocialAuthenticationFilter)
     */
    @Override
    public void process(SocialAuthenticationFilter socialAuthenticationFilter) {
        socialAuthenticationFilter.setAuthenticationSuccessHandler(imoocAuthenticationSuccessHandler);
    }

}
