package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.dto.DeptDTO;
import java.util.List;

public interface DeptBizService {
    IPage<DeptDTO> pageByTenantCode(String tenantCode, int pageNum, int pageSize);
    List<DeptDTO> listByTenantCode(String tenantCode);
    List<DeptDTO> listByDomainCode(String domainCode);
    List<DeptDTO> listByOrgCode(String orgCode);
    DeptDTO getByDeptCode(String deptCode);
    List<DeptDTO> list();
    boolean delete(String deptCode);
    boolean create(DeptDTO dto);
    boolean update(DeptDTO dto);
}
