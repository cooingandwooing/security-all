/**
 *
 */
package com.github.qingyejiazhu.securitycore.properties;

import org.springframework.social.security.SocialAuthenticationFilter;

/**
 * @author xiaofenggao
 *
 */
public class SocialProperties {
    // 这里是拦截地址 social 登陆的
    /**
    * @see SocialAuthenticationFilter# DEFAULT_FILTER_PROCESSES_URL
    * */
    private String filterProcessesUrl = "/auth";

    private QQProperties qq = new QQProperties();

    private WeixinProperties weixin = new WeixinProperties();

    public WeixinProperties getWeixin() {
        return weixin;
    }

    public void setWeixin(WeixinProperties weixin) {
        this.weixin = weixin;
    }

    public QQProperties getQq() {
        return qq;
    }

    public void setQq(QQProperties qq) {
        this.qq = qq;
    }

    public String getFilterProcessesUrl() {
        return filterProcessesUrl;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.filterProcessesUrl = filterProcessesUrl;
    }
}
