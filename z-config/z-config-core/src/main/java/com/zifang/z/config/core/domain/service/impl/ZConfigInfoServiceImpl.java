package com.zifang.z.config.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.zifang.z.config.core.domain.entity.ZConfigInfo;
import com.zifang.z.config.core.domain.mapper.ZConfigInfoMapper;
import com.zifang.z.config.core.domain.service.IZConfigInfoService;

/**
 * 主配置表 服务实现类
 * @author auto-generated
 */
@Service
public class ZConfigInfoServiceImpl extends ServiceImpl<ZConfigInfoMapper, ZConfigInfo> implements IZConfigInfoService {

    @Override
    public ZConfigInfo queryConfig(String namespace, String group, String dataId) {
        LambdaQueryWrapper<ZConfigInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ZConfigInfo::getNamespace,namespace);
        queryWrapper.eq(ZConfigInfo::getGroup,group);
        queryWrapper.eq(ZConfigInfo::getDataId,dataId);
        return getOne(queryWrapper);
    }
}