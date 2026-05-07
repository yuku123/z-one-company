package com.zifang.ctc.core.service;

import com.zifang.ctc.core.domain.entity.UserOrgRel;
import java.util.List;
import java.util.Map;

public interface UserOrgRelBizService {
    List<Map<String, Object>> usersByGroup(String groupCode);
    List<Map<String, Object>> usersByDept(String deptCode);
    void bind(UserOrgRel rel);
    void clearUser(Long userId);
}
