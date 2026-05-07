package com.zifang.ctc.core.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zifang.ctc.core.domain.entity.AppMenu;
import java.util.List;

public interface IAppMenuService extends IService<AppMenu> {
    List<AppMenu> listByApp(String appCode);
}
