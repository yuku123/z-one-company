package com.zifang.ctc.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.domain.entity.UserOrgRel;
import com.zifang.ctc.core.domain.service.IUserOrgRelService;
import com.zifang.ctc.core.domain.service.IUserService;
import com.zifang.ctc.core.service.UserOrgRelBizService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserOrgRelBizServiceImpl implements UserOrgRelBizService {

    @Resource
    private IUserOrgRelService userOrgRelService;

    @Resource
    private IUserService userService;

    @Override
    public List<Map<String, Object>> usersByGroup(String groupCode) {
        return getUsers(UserOrgRel::getGroupCode, groupCode);
    }

    @Override
    public List<Map<String, Object>> usersByDept(String deptCode) {
        return getUsers(UserOrgRel::getDeptCode, deptCode);
    }

    private List<Map<String, Object>> getUsers(
            com.baomidou.mybatisplus.core.toolkit.support.SFunction<UserOrgRel, String> field, String code) {
        List<UserOrgRel> rels = userOrgRelService.list(
            new LambdaQueryWrapper<UserOrgRel>().eq(field, code));
        List<Long> userIds = rels.stream().map(UserOrgRel::getUserId).distinct().collect(Collectors.toList());
        if (userIds.isEmpty()) return List.of();
        List<User> users = userService.listByIds(userIds);
        return users.stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("userName", u.getUserName());
            m.put("nickName", u.getNickName());
            m.put("realName", u.getRealName());
            return m;
        }).collect(Collectors.toList());
    }

    @Override
    public void bind(UserOrgRel rel) {
        userOrgRelService.save(rel);
    }

    @Override
    public void clearUser(Long userId) {
        userOrgRelService.deleteByUserId(userId);
    }
}
