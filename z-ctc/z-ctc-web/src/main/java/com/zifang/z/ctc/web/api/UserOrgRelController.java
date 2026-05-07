package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zifang.ctc.core.domain.entity.UserOrgRel;
import com.zifang.ctc.core.domain.entity.User;
import com.zifang.ctc.core.domain.service.IUserOrgRelService;
import com.zifang.ctc.core.domain.mapper.UserMapper;
import com.zifang.util.core.meta.Result;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-org")
public class UserOrgRelController {

    @Resource
    private IUserOrgRelService userOrgRelService;

    @Resource
    private UserMapper userMapper;

    @GetMapping("/users-by-group")
    public Result<List<Map<String, Object>>> usersByGroup(@RequestParam String groupCode) {
        return getUsers(UserOrgRel::getGroupCode, groupCode);
    }

    @GetMapping("/users-by-dept")
    public Result<List<Map<String, Object>>> usersByDept(@RequestParam String deptCode) {
        return getUsers(UserOrgRel::getDeptCode, deptCode);
    }

    private Result<List<Map<String, Object>>> getUsers(
            com.baomidou.mybatisplus.core.toolkit.support.SFunction<UserOrgRel, String> field, String code) {
        List<UserOrgRel> rels = userOrgRelService.list(
            new LambdaQueryWrapper<UserOrgRel>().eq(field, code));
        List<Long> userIds = rels.stream().map(UserOrgRel::getUserId).distinct().collect(Collectors.toList());
        if (userIds.isEmpty()) return Result.success(List.of());
        List<User> users = userMapper.selectBatchIds(userIds);
        return Result.success(users.stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("userName", u.getUserName());
            m.put("nickName", u.getNickName());
            m.put("realName", u.getRealName());
            return m;
        }).collect(Collectors.toList()));
    }

    @PostMapping("/bind")
    public Result<String> bind(@RequestBody UserOrgRel rel) {
        userOrgRelService.save(rel);
        return Result.success();
    }

    @DeleteMapping("/user/{userId}")
    public Result<String> clearUser(@PathVariable Long userId) {
        userOrgRelService.deleteByUserId(userId);
        return Result.success();
    }
}
