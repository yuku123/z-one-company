package com.zifang.ctc.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.ctc.core.domain.entity.Dictionary;
import com.zifang.ctc.core.domain.mapper.DictionaryMapper;
import com.zifang.ctc.core.domain.service.IDictionaryService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements IDictionaryService {

    @Override
    public List<Dictionary> listByTenant(String tenantCode) {
        LambdaQueryWrapper<Dictionary> w = new LambdaQueryWrapper<>();
        w.eq(Dictionary::getTenantCode, tenantCode).or().isNull(Dictionary::getTenantCode);
        w.orderByAsc(Dictionary::getSortOrder);
        return list(w);
    }

    @Override
    public List<String> listCategories(String tenantCode) {
        return listByTenant(tenantCode).stream()
            .map(Dictionary::getCategory).distinct().collect(Collectors.toList());
    }

    @Override
    public void initBuiltin(String tenantCode, String domainCode) {
        List<Dictionary> items = new ArrayList<>();
        String[][] seeds = {
            // category, key, value, sort
            {"gender", "M", "男", "1"},
            {"gender", "F", "女", "2"},
            {"status", "1", "启用", "1"},
            {"status", "0", "停用", "2"},
            {"yes_no", "Y", "是", "1"},
            {"yes_no", "N", "否", "2"},
        };
        int sort = 1;
        for (String[] s : seeds) {
            Dictionary d = new Dictionary();
            d.setCategory(s[0]); d.setDictKey(s[1]); d.setDictValue(s[2]);
            d.setSortOrder(Integer.parseInt(s[3]));
            d.setTenantCode(tenantCode); d.setDomainCode(domainCode);
            d.setIsBuiltin(1); d.setStatus(1);
            d.setGmtCreate(LocalDateTime.now());
            items.add(d);
        }
        saveBatch(items);
    }

    @Override
    public boolean hasInit(String tenantCode) {
        return count(new LambdaQueryWrapper<Dictionary>()
            .eq(Dictionary::getTenantCode, tenantCode)) > 0;
    }
}
