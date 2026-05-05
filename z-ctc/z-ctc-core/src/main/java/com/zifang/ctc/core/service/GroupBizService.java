package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.dto.GroupDTO;

import java.util.List;

public interface GroupBizService {
    List<GroupDTO> list();
    IPage<GroupDTO> page(GroupDTO group);
    List<GroupDTO> listByTenantId(Long tenantId);
    List<GroupDTO> listByDomainId(Long domainId);
    List<GroupDTO> listByOrgId(Long orgId);
    List<GroupDTO> listByDeptId(Long deptId);
    GroupDTO getById(Long id);
    void add(GroupDTO group);
    void update(GroupDTO group);
    void delete(Long id);
}
