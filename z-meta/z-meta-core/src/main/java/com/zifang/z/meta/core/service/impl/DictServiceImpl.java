package com.zifang.z.meta.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.meta.core.entity.ZDictType;
import com.zifang.z.meta.core.entity.ZDictItem;
import com.zifang.z.meta.core.mapper.ZDictTypeMapper;
import com.zifang.z.meta.core.mapper.ZDictItemMapper;
import com.zifang.z.meta.core.service.IDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典 Service 实现
 */
@Service
public class DictServiceImpl extends ServiceImpl<ZDictTypeMapper, ZDictType> implements IDictService {

    @Autowired
    private ZDictItemMapper dictItemMapper;

    @Override
    public IPage<ZDictType> pageDictType(Page<ZDictType> page, ZDictType dictType) {
        LambdaQueryWrapper<ZDictType> wrapper = new LambdaQueryWrapper<>();
        if (dictType != null) {
            if (dictType.getTenantId() != null) {
                wrapper.eq(ZDictType::getTenantId, dictType.getTenantId());
            }
            if (StringUtils.hasText(dictType.getDictCode())) {
                wrapper.like(ZDictType::getDictCode, dictType.getDictCode());
            }
            if (StringUtils.hasText(dictType.getDictName())) {
                wrapper.like(ZDictType::getDictName, dictType.getDictName());
            }
            if (dictType.getStatus() != null) {
                wrapper.eq(ZDictType::getStatus, dictType.getStatus());
            }
        }
        wrapper.orderByDesc(ZDictType::getGmtCreate);
        return page(page, wrapper);
    }

    @Override
    public ZDictType getByDictCode(Long tenantId, String dictCode) {
        return getOne(new LambdaQueryWrapper<ZDictType>()
                .eq(ZDictType::getTenantId, tenantId)
                .eq(ZDictType::getDictCode, dictCode));
    }

    @Override
    public List<ZDictItem> listByDictId(Long dictId) {
        return dictItemMapper.selectList(new LambdaQueryWrapper<ZDictItem>()
                .eq(ZDictItem::getDictId, dictId)
                .orderByAsc(ZDictItem::getSortOrder));
    }

    @Override
    public List<ZDictItem> listByDictCode(String dictCode) {
        // 先查询字典类型
        ZDictType dictType = getByDictCode(1L, dictCode); // TODO: 从上下文获取租户ID
        if (dictType == null) {
            return null;
        }
        return listByDictId(dictType.getId());
    }
}