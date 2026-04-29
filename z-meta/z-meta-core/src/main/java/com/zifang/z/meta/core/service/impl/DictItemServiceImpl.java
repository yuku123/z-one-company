package com.zifang.z.meta.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.meta.core.entity.ZDictItem;
import com.zifang.z.meta.core.mapper.ZDictItemMapper;
import com.zifang.z.meta.core.service.IDictItemService;
import org.springframework.stereotype.Service;

/**
 * 字典项 Service 实现
 */
@Service
public class DictItemServiceImpl extends ServiceImpl<ZDictItemMapper, ZDictItem> implements IDictItemService {
}