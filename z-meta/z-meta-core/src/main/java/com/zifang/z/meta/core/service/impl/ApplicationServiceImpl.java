package com.zifang.z.meta.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.meta.core.entity.ZApplication;
import com.zifang.z.meta.core.mapper.ZApplicationMapper;
import com.zifang.z.meta.core.service.IApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 应用 Service 实现
 */
@Service
public class ApplicationServiceImpl extends ServiceImpl<ZApplicationMapper, ZApplication> implements IApplicationService {

    @Override
    public IPage<ZApplication> pageApplication(Page<ZApplication> page, ZApplication application) {
        LambdaQueryWrapper<ZApplication> wrapper = new LambdaQueryWrapper<>();
        if (application != null) {
            if (application.getTenantId() != null) {
                wrapper.eq(ZApplication::getTenantId, application.getTenantId());
            }
            if (StringUtils.hasText(application.getAppCode())) {
                wrapper.like(ZApplication::getAppCode, application.getAppCode());
            }
            if (StringUtils.hasText(application.getAppName())) {
                wrapper.like(ZApplication::getAppName, application.getAppName());
            }
            if (StringUtils.hasText(application.getAppType())) {
                wrapper.eq(ZApplication::getAppType, application.getAppType());
            }
            if (application.getStatus() != null) {
                wrapper.eq(ZApplication::getStatus, application.getStatus());
            }
        }
        wrapper.orderByDesc(ZApplication::getGmtCreate);
        return page(page, wrapper);
    }

    @Override
    public ZApplication getByAppCode(Long tenantId, String appCode) {
        return getOne(new LambdaQueryWrapper<ZApplication>()
                .eq(ZApplication::getTenantId, tenantId)
                .eq(ZApplication::getAppCode, appCode));
    }
}