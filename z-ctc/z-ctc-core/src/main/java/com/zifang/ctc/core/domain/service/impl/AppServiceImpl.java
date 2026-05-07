package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.AppDO;
import com.zifang.ctc.core.domain.mapper.AppMapper;
import com.zifang.ctc.core.domain.service.IAppService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, AppDO> implements IAppService {
    @Override
    public List<AppDO> listByTenant(String tenantCode) {
        return list(new LambdaQueryWrapper<AppDO>().eq(AppDO::getTenantCode, tenantCode));
    }
    @Override
    public List<AppDO> listByDomain(String domainCode) {
        return list(new LambdaQueryWrapper<AppDO>().eq(AppDO::getDomainCode, domainCode));
    }
}
