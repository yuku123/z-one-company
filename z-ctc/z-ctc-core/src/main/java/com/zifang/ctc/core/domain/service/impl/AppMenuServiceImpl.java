package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.AppMenu;
import com.zifang.ctc.core.domain.mapper.AppMenuMapper;
import com.zifang.ctc.core.domain.service.IAppMenuService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppMenuServiceImpl extends ServiceImpl<AppMenuMapper, AppMenu> implements IAppMenuService {
    @Override
    public List<AppMenu> listByApp(String appCode) {
        return list(new LambdaQueryWrapper<AppMenu>().eq(AppMenu::getAppCode, appCode)
            .orderByAsc(AppMenu::getSortOrder));
    }
}
