package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.dto.GroupDTO;
import java.util.List;

public interface GroupBizService {
    IPage<GroupDTO> pageByTenantCode(String tenantCode, int pageNum, int pageSize);
    List<GroupDTO> listByTenantCode(String tenantCode);
    List<GroupDTO> listByDomainCode(String domainCode);
    List<GroupDTO> listByOrgCode(String orgCode);
    List<GroupDTO> listByDeptCode(String deptCode);
    GroupDTO getByGroupCode(String groupCode);
    List<GroupDTO> list();
    boolean delete(String groupCode);
    boolean create(GroupDTO dto);
    boolean update(GroupDTO dto);
}
