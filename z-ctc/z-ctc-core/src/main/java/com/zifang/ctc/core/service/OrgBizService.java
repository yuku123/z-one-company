package com.zifang.ctc.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.dto.OrgDTO;
import java.util.List;

public interface OrgBizService {
    IPage<OrgDTO> pageByTenantCode(String tenantCode, int pageNum, int pageSize);
    List<OrgDTO> listByTenantCode(String tenantCode);
    List<OrgDTO> listByDomainCode(String domainCode);
    OrgDTO getByOrgCode(String orgCode);
    List<OrgDTO> list();
    boolean delete(String orgCode);
    boolean create(OrgDTO dto);
    boolean update(OrgDTO dto);
}
