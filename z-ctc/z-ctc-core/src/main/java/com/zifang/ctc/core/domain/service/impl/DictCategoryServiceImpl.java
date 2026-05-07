package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.DictCategory;
import com.zifang.ctc.core.domain.mapper.DictCategoryMapper;
import com.zifang.ctc.core.domain.service.IDictCategoryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DictCategoryServiceImpl extends ServiceImpl<DictCategoryMapper, DictCategory> implements IDictCategoryService {
    @Override
    public List<DictCategory> listByTenant(String tenantCode) {
        return list(new LambdaQueryWrapper<DictCategory>().eq(DictCategory::getTenantCode, tenantCode));
    }
}
