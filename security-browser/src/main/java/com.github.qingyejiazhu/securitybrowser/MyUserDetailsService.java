package com.github.qingyejiazhu.securitybrowser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author gaoxiaofeng
 * @description
 * @date 2019-06-23 18:41
 */
@Component
public class MyUserDetailsService implements UserDetailsService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 可以从任何地方获取数据
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查找用户信息
        logger.info("登录用户名", username);
        // 写死一个密码，赋予一个admin权限
        /*return new User(username, "{noop}123456",
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));*/
        String password = passwordEncoder.encode("123456");
        logger.info("数据库密码{}", password);
        User admin = new User(username, password,
                true, true, true, true,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
        return admin;
    }
}
