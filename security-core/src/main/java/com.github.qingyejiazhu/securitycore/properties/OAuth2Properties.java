package com.github.qingyejiazhu.securitycore.properties;

/**
 * @author : gxf
 * @version : V1.0
 */
public class OAuth2Properties {
    private OAuth2ClientProperties[] clients = {};
    // jwt唯一的安全性是下面的密签
    private String jwtSigningKey = "mrcode";

    public OAuth2ClientProperties[] getClients() {
        return clients;
    }

    public void setClients(OAuth2ClientProperties[] clients) {
        this.clients = clients;
    }

    public String getJwtSigningKey() {
        return jwtSigningKey;
    }

    public void setJwtSigningKey(String jwtSigningKey) {
        this.jwtSigningKey = jwtSigningKey;
    }
}
