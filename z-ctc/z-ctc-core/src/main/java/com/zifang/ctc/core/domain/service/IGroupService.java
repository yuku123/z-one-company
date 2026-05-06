package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.GroupDO;
import java.util.List;

public interface IGroupService extends IService<GroupDO> {
    IPage<GroupDO> pageByTenantCode(Page<GroupDO> page, String tenantCode);
    List<GroupDO> listByTenantCode(String tenantCode);
    List<GroupDO> listByDomainCode(String domainCode);
    List<GroupDO> listByOrgCode(String orgCode);
    List<GroupDO> listByDeptCode(String deptCode);
    GroupDO getByGroupCode(String groupCode);
    Long getIdByGroupCode(String groupCode);
    boolean deleteByGroupCode(String groupCode);
}
