package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.Dictionary;
import java.util.List;

public interface IDictionaryService extends IService<Dictionary> {
    List<Dictionary> listByTenant(String tenantCode);
    List<String> listCategories(String tenantCode);
    void initBuiltin(String tenantCode, String domainCode);
    boolean hasInit(String tenantCode);
}
