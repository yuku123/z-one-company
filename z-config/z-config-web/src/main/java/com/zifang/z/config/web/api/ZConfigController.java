package com.zifang.z.config.web.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.zifang.util.core.meta.Result;
import com.zifang.util.core.meta.page.Pageable;
import com.zifang.z.config.common.model.config.ZConfigListRequest;
import com.zifang.z.config.common.model.config.ZConfigPageRequest;
import com.zifang.z.config.common.model.ZConfigDTO;
import com.zifang.z.config.common.model.config.ZConfigSaveRequest;
import com.zifang.z.config.common.model.config.ZConfigQueryRequest;
import com.zifang.z.config.common.model.config.ZConfigHistoryPageRequest;
import com.zifang.z.config.core.domain.entity.ZConfigInfo;
import com.zifang.z.config.core.domain.entity.ZConfigInfoHistory;
import com.zifang.z.config.core.domain.mapper.ZConfigInfoMapper;
import com.zifang.z.config.core.domain.service.IZConfigInfoHistoryService;
import com.zifang.z.config.core.service.ConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/config")
@Tag(name = "001_配置管理")

public class ZConfigController{

    @Resource
    private ConfigService configService;

    @Resource
    private ZConfigInfoMapper configInfoMapper;

    @Resource
    private IZConfigInfoHistoryService zConfigInfoHistoryService;


    @PostMapping("/saveConfig")
    @Operation(summary = "001_保存配置")
    public Result<String> saveConfig(@RequestBody ZConfigSaveRequest zConfigSaveRequest) {
        return configService.saveConfig(zConfigSaveRequest);
    }

    @PostMapping("/getConfig")
    @Operation(summary = "002_获取配置")
    
    public Result<String> getConfig(@RequestBody ZConfigQueryRequest configRequest) {
        return configService.getConfig(configRequest);
    }


    @PostMapping("/pageConfig")
    @Operation(summary = "003_分页获取配置信息")
    
    public Result<Pageable<ZConfigDTO>> pageConfig(@RequestBody ZConfigPageRequest request) {
        return configService.pageConfig(request);
    }


    @PostMapping("/listConfig")
    @Operation(summary = "004_列表获取配置信息")
    
    public Result<List<ZConfigDTO>> listConfig(@RequestBody ZConfigListRequest request) {
        return configService.listConfig(request);
    }

    @PostMapping("/delete")
    @Operation(summary = "005_删除配置")
    
    public Result<String> deleteConfig(@RequestBody ZConfigQueryRequest request) {
        return configService.deleteConfig(request);
    }

    @GetMapping("/groupList")
    @Operation(summary = "006_获取所有Group列表")
    
    public Result<List<String>> groupList() {
        QueryWrapper<ZConfigInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct `group`");
        List<ZConfigInfo> list = configInfoMapper.selectList(queryWrapper);
        List<String> groups = list.stream().map(ZConfigInfo::getGroup).collect(Collectors.toList());
        return Result.success(groups);
    }

    @PostMapping("/history/page")
    @Operation(summary = "007_分页查询配置变更历史")

    public Result<Pageable<ZConfigInfoHistory>> historyPage(@RequestBody ZConfigHistoryPageRequest request) {
        Page<ZConfigInfoHistory> page = new Page<>(request.getPageNum(), request.getPageSize());
        QueryWrapper<ZConfigInfoHistory> queryWrapper = new QueryWrapper<>();

        // 命名空间筛选
        if(request.getNamespace() != null){
            queryWrapper.eq("namespace", request.getNamespace());
        }
        // Group筛选
        if(request.getGroup() != null && !request.getGroup().isEmpty()){
            queryWrapper.eq("`group`", request.getGroup());
        }
        // DataID搜索
        if(request.getSearch() != null && !request.getSearch().isEmpty()){
            queryWrapper.like("data_id", request.getSearch());
        }
        // 按创建时间倒序
        queryWrapper.orderByDesc("gmt_create");

        IPage<ZConfigInfoHistory> pageData = zConfigInfoHistoryService.page(page, queryWrapper);

        Pageable<ZConfigInfoHistory> pageable = new Pageable<>();
        pageable.setTotal(page.getTotal());
        pageable.setCurrent(page.getCurrent());
        pageable.setSize(page.getSize());
        pageable.setRecords(pageData.getRecords());

        return Result.success(pageable);
    }

    /**
     * 回滚到指定历史版本[Retrieve]操作
     */
    @PostMapping("/rollback")
    public Result<String> rollback(@RequestBody com.zifang.z.config.common.model.config.ZConfigSaveRequest request) {
        return configService.saveConfig(request);
    }

    @GetMapping("/namespaceList")
    @Operation(summary = "008_获取所有命名空间列表")
    
    public Result<List<String>> namespaceList() {
        QueryWrapper<ZConfigInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct namespace");
        List<ZConfigInfo> list = configInfoMapper.selectList(queryWrapper);
        List<String> namespaces = list.stream().map(ZConfigInfo::getNamespace).collect(Collectors.toList());
        return Result.success(namespaces);
    }

}
