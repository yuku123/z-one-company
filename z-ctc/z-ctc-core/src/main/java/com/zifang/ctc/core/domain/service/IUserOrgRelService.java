package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.UserOrgRel;
import java.util.List;

public interface IUserOrgRelService extends IService<UserOrgRel> {
    List<UserOrgRel> listByGroupCode(String groupCode);
    List<UserOrgRel> listByUserId(Long userId);
    void deleteByUserId(Long userId);
}
