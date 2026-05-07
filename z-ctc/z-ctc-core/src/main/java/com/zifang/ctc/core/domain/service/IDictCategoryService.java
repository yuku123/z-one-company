package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.DictCategory;
import java.util.List;

public interface IDictCategoryService extends IService<DictCategory> {
    List<DictCategory> listByTenant(String tenantCode);
}
