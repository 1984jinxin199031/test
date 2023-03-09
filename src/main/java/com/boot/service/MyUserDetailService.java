package com.boot.service;

import com.boot.dao.UserDao;
import com.boot.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

//自定义 UserDetailService 实现
@Service
public class MyUserDetailService implements UserDetailsService, UserDetailsPasswordService {


    private final UserDao userDao;

    @Autowired
    public MyUserDetailService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userDao.loadUserByUsername(username);
        if (ObjectUtils.isEmpty(user)) throw new RuntimeException("用户不存在!");
        user.setRoles(userDao.getRolesByUid(user.getId()));
        return user;
    }

    @Override //默认使用DelegatingPasswordEncoder 默认使用相当最安全密码加密 Bcrypt
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        Integer result = userDao.updatePassword(user.getUsername(), newPassword);
        if (result == 1) {
            ((User) user).setPassword(newPassword);
        }
        return user;
    }

}
