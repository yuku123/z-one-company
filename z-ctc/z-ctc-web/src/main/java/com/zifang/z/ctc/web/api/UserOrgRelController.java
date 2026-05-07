package com.zifang.z.ctc.web.api;

import com.zifang.ctc.core.service.UserOrgRelBizService;
import com.zifang.util.core.meta.Result;
import com.zifang.z.ctc.web.api.request.UserOrgBindReq;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-org")
public class UserOrgRelController {

    @Resource
    private UserOrgRelBizService userOrgRelBizService;

    @GetMapping("/users-by-group")
    public Result<List<Map<String, Object>>> usersByGroup(@RequestParam String groupCode) {
        return Result.success(userOrgRelBizService.usersByGroup(groupCode));
    }

    @GetMapping("/users-by-dept")
    public Result<List<Map<String, Object>>> usersByDept(@RequestParam String deptCode) {
        return Result.success(userOrgRelBizService.usersByDept(deptCode));
    }

    @PostMapping("/bind")
    public Result<String> bind(@RequestBody UserOrgBindReq req) {
        com.zifang.ctc.core.domain.entity.UserOrgRel rel = new com.zifang.ctc.core.domain.entity.UserOrgRel();
        org.springframework.beans.BeanUtils.copyProperties(req, rel);
        userOrgRelBizService.bind(rel);
        return Result.success();
    }

    @DeleteMapping("/user/{userId}")
    public Result<String> clearUser(@PathVariable Long userId) {
        userOrgRelBizService.clearUser(userId);
        return Result.success();
    }
}
