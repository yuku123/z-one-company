package com.zifang.z.meta.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.z.meta.core.entity.ZDictType;
import com.zifang.z.meta.core.entity.ZDictItem;

import java.util.List;

/**
 * 字典 Service
 */
public interface IDictService extends IService<ZDictType> {

    /**
     * 分页查询字典类型
     */
    IPage<ZDictType> pageDictType(Page<ZDictType> page, ZDictType dictType);

    /**
     * 根据租户和字典编码查询
     */
    ZDictType getByDictCode(Long tenantId, String dictCode);

    /**
     * 根据字典ID查询字典项
     */
    List<ZDictItem> listByDictId(Long dictId);

    /**
     * 根据字典编码查询字典项
     */
    List<ZDictItem> listByDictCode(String dictCode);
}