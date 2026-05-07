package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.AppDO;
import java.util.List;

public interface IAppService extends IService<AppDO> {
    List<AppDO> listByTenant(String tenantCode);
    List<AppDO> listByDomain(String domainCode);
}
