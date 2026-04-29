package com.zifang.z.meta.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.meta.core.entity.ZApi;
import com.zifang.z.meta.core.mapper.ZApiMapper;
import com.zifang.z.meta.core.service.IApiService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 接口 Service 实现
 */
@Service
public class ApiServiceImpl extends ServiceImpl<ZApiMapper, ZApi> implements IApiService {

    @Override
    public IPage<ZApi> pageApi(Page<ZApi> page, ZApi api) {
        LambdaQueryWrapper<ZApi> wrapper = new LambdaQueryWrapper<>();
        if (api != null) {
            if (api.getAppId() != null) {
                wrapper.eq(ZApi::getAppId, api.getAppId());
            }
            if (StringUtils.hasText(api.getApiPath())) {
                wrapper.like(ZApi::getApiPath, api.getApiPath());
            }
            if (StringUtils.hasText(api.getApiName())) {
                wrapper.like(ZApi::getApiName, api.getApiName());
            }
            if (StringUtils.hasText(api.getApiMethod())) {
                wrapper.eq(ZApi::getApiMethod, api.getApiMethod());
            }
            if (api.getStatus() != null) {
                wrapper.eq(ZApi::getStatus, api.getStatus());
            }
            if (api.getDeprecated() != null) {
                wrapper.eq(ZApi::getDeprecated, api.getDeprecated());
            }
        }
        wrapper.orderByDesc(ZApi::getGmtCreate);
        return page(page, wrapper);
    }

    @Override
    public List<ZApi> listByAppId(Long appId) {
        return list(new LambdaQueryWrapper<ZApi>()
                .eq(ZApi::getAppId, appId)
                .orderByDesc(ZApi::getGmtCreate));
    }
}