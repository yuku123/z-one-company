package com.zifang.z.meta.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.z.meta.core.entity.ZApi;

import java.util.List;

/**
 * 接口 Service
 */
public interface IApiService extends IService<ZApi> {

    /**
     * 分页查询接口
     */
    IPage<ZApi> pageApi(Page<ZApi> page, ZApi api);

    /**
     * 根据应用ID查询接口列表
     */
    List<ZApi> listByAppId(Long appId);
}