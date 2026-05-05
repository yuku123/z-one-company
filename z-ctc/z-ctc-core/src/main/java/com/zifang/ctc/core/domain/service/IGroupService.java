package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.GroupDO;

import java.util.List;

public interface IGroupService extends IService<GroupDO> {

    IPage<GroupDO> page(Page<GroupDO> page, GroupDO groupDO);

    List<GroupDO> listByTenantId(Long tenantId);

    List<GroupDO> listByDomainId(Long domainId);

    List<GroupDO> listByOrgId(Long orgId);

    List<GroupDO> listByDeptId(Long deptId);

    GroupDO getByGroupId(Long groupId);

    boolean add(GroupDO groupDO);

    boolean update(GroupDO groupDO);

    boolean delete(Long id);
}
