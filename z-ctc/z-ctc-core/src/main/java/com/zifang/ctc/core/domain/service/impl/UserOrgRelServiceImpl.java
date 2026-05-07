package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.UserOrgRel;
import com.zifang.ctc.core.domain.mapper.UserOrgRelMapper;
import com.zifang.ctc.core.domain.service.IUserOrgRelService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserOrgRelServiceImpl extends ServiceImpl<UserOrgRelMapper, UserOrgRel> implements IUserOrgRelService {

    @Override
    public List<UserOrgRel> listByGroupCode(String groupCode) {
        return list(new LambdaQueryWrapper<UserOrgRel>().eq(UserOrgRel::getGroupCode, groupCode));
    }

    @Override
    public List<UserOrgRel> listByUserId(Long userId) {
        return list(new LambdaQueryWrapper<UserOrgRel>().eq(UserOrgRel::getUserId, userId));
    }

    @Override
    public void deleteByUserId(Long userId) {
        remove(new LambdaQueryWrapper<UserOrgRel>().eq(UserOrgRel::getUserId, userId));
    }
}
