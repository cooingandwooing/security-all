package com.github.qingyejiazhu.securityapp.social;

import com.github.qingyejiazhu.securityapp.AppConstants;
import com.github.qingyejiazhu.securityapp.AppSecretException;
import com.github.qingyejiazhu.securitycore.social.SignUpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * @author gxf
 * @version 1.0.1 2018/8/9 14:28
 * @date 2018/8/9 14:28
 * @see ProviderSignInUtils 模拟其中部分的功能
 * @since 1.0
 */
@Component
public class AppSignUpUtils implements SignUpUtils {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    // 目前为止都是自动配置的，直接获取即可
    @Autowired
    private UsersConnectionRepository usersConnectionRepository;
    // 用于定位connectionfactory
    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    @Override
    public void saveConnection(ServletWebRequest request, ConnectionData connectionData) {
        redisTemplate.opsForValue().set(getKey(request), connectionData);
    }

    /**
     * @param userId
     * @param request
     * 登陆注册 /user/regist
     * @see ProviderSignInAttempt# addConnection(String, ConnectionFactoryLocator, UsersConnectionRepository)
     */
    @Override
    public void doPostSignUp(String userId, ServletWebRequest request) {
        String key = getKey(request);
        if(!redisTemplate.hasKey(key)){
            throw new AppSecretException("无法找到缓存的第三方用户社交账号信息");
        }
        ConnectionData connectionData = (ConnectionData) redisTemplate.opsForValue().get(key);
        usersConnectionRepository.createConnectionRepository(userId).addConnection(getConnection(connectionFactoryLocator, connectionData));
        redisTemplate.delete(key);//----------
    }

    public Connection<?> getConnection(ConnectionFactoryLocator connectionFactoryLocator, ConnectionData connectionData) {
        // 根据ProviderId qq还是微信 的就拿连接工厂
        return connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId()).createConnection(connectionData);
    }

    private String getKey(ServletWebRequest request) {
        String deviceId = request.getHeader(AppConstants.DEFAULT_HEADER_DEVICE_ID);
        if (StringUtils.isBlank(deviceId)) {
            throw new AppSecretException("设备id参数不能为空");
        }
        return "zhanlu:security:social.connect." + deviceId;
    }
}
