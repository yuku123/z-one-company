package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.domain.mapper.UserMapper;
import com.zifang.ctc.core.domain.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User selectByUserName(String userName) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>().eq(User::getUserName, userName);
        return getOne(lambdaQueryWrapper);
    }

    @Override
    public User selectByPhone(String phone) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
    }

    @Override
    public User selectByEmail(String email) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
    }

    @Override
    public long countByUserName(String userName) {
        return count(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
    }

    @Override
    public IPage<User> selectPage(Page<User> page, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUserName, keyword)
                   .or()
                   .like(User::getPhone, keyword)
                   .or()
                   .like(User::getEmail, keyword);
        }
        return page(page, wrapper);
    }
}
