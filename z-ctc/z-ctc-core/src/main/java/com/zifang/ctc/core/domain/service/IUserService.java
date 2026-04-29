package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.User;

public interface IUserService extends IService<User> {

    User selectByUserName(String userName);

    User selectByPhone(String phone);

    User selectByEmail(String email);

    long countByUserName(String userName);

    IPage<User> selectPage(Page<User> page, String keyword);
}
