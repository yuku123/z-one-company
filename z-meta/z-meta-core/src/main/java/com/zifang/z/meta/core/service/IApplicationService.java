package com.zifang.z.meta.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.z.meta.core.entity.ZApplication;

/**
 * 应用 Service
 */
public interface IApplicationService extends IService<ZApplication> {

    /**
     * 分页查询应用
     */
    IPage<ZApplication> pageApplication(Page<ZApplication> page, ZApplication application);

    /**
     * 根据租户和应用编码查询
     */
    ZApplication getByAppCode(Long tenantId, String appCode);
}