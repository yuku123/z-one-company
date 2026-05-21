package com.zifang.z.agent.engine.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifang.z.agent.engine.app.dto.AgentAppDto;
import com.zifang.z.agent.engine.app.entity.AgentApp;
import com.zifang.z.agent.engine.app.entity.AgentAppDraft;
import com.zifang.z.agent.engine.app.entity.AgentAppVersion;
import com.zifang.z.agent.engine.app.mapper.AgentAppDraftMapper;
import com.zifang.z.agent.engine.app.mapper.AgentAppMapper;
import com.zifang.z.agent.engine.app.mapper.AgentAppVersionMapper;
import com.zifang.z.agent.engine.app.service.AgentAppService;
import com.zifang.z.agent.engine.app.dto.AgentAppReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgentAppServiceImpl extends ServiceImpl<AgentAppMapper, AgentApp> implements AgentAppService {

    @Resource
    private AgentAppVersionMapper versionMapper;

    @Resource
    private AgentAppDraftMapper draftMapper;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public IPage<AgentAppDto> pageResp(String keyword, String status, int pageNum, int pageSize) {
        LambdaQueryWrapper<AgentApp> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.like(AgentApp::getAppName, keyword).or()
                   .like(AgentApp::getAppCode, keyword);
        }
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq(AgentApp::getStatus, status);
        }
        wrapper.eq(AgentApp::getIsDeleted, 0);
        wrapper.orderByDesc(AgentApp::getGmtCreate);
        IPage<AgentApp> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        return page.convert(this::toDto);
    }

    @Override
    public AgentAppDto getRespByAppCode(String appCode) {
        return toDto(getByAppCode(appCode));
    }

    @Override
    public AgentAppDto createResp(AgentAppReq req, ObjectMapper objectMapper) {
        AgentApp app = toEntity(req, objectMapper);
        if (StringUtils.isEmpty(app.getAppCode())) {
            app.setAppCode("app_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        }
        app.setStatus("DRAFT");
        app.setGmtCreate(LocalDateTime.now());
        app.setGmtModified(LocalDateTime.now());
        app.setIsDeleted(0);
        this.save(app);
        return toDto(app);
    }

    @Override
    public AgentAppDto updateResp(AgentAppReq req, ObjectMapper objectMapper) {
        AgentApp existing = getByAppCode(req.getAppCode());
        if (existing == null) {
            throw new RuntimeException("App not found: " + req.getAppCode());
        }
        AgentApp app = toEntity(req, objectMapper);
        app.setId(existing.getId());
        app.setGmtModified(LocalDateTime.now());
        this.updateById(app);
        return toDto(this.getById(existing.getId()));
    }

    @Override
    public AgentApp getByAppCode(String appCode) {
        LambdaQueryWrapper<AgentApp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentApp::getAppCode, appCode);
        wrapper.eq(AgentApp::getIsDeleted, 0);
        return this.getOne(wrapper);
    }

    @Override
    public AgentApp create(AgentApp app) {
        if (StringUtils.isEmpty(app.getAppCode())) {
            app.setAppCode("app_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        }
        app.setStatus("DRAFT");
        app.setGmtCreate(LocalDateTime.now());
        app.setGmtModified(LocalDateTime.now());
        app.setIsDeleted(0);
        this.save(app);
        return app;
    }

    @Override
    public AgentApp update(AgentApp app) {
        app.setGmtModified(LocalDateTime.now());
        this.updateById(app);
        return app;
    }

    @Override
    public void remove(Long id) {
        AgentApp app = this.getById(id);
        if (app != null) {
            app.setIsDeleted(1);
            this.updateById(app);
        }
    }

    @Override
    public void publish(String appCode) {
        AgentApp app = getByAppCode(appCode);
        if (app == null) {
            throw new RuntimeException("App not found: " + appCode);
        }
        app.setStatus("PUBLISHED");
        updateById(app);
    }

    @Override
    public List<AgentAppDto> listVersionResp(String appCode) {
        List<AgentAppVersion> list = listVersions(appCode);
        return list.stream().map(v -> {
            AgentAppDto dto = new AgentAppDto();
            dto.setAppCode(v.getAppCode());
            dto.setGmtCreate(v.getGmtCreate() != null ? v.getGmtCreate().format(DF) : null);
            dto.setStatus(v.getVersion());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AgentAppVersion> listVersions(String appCode) {
        LambdaQueryWrapper<AgentAppVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentAppVersion::getAppCode, appCode);
        wrapper.orderByDesc(AgentAppVersion::getGmtCreate);
        return versionMapper.selectList(wrapper);
    }

    @Override
    public AgentAppVersion createVersion(AgentAppVersion version) {
        version.setGmtCreate(LocalDateTime.now());
        version.setGmtModified(LocalDateTime.now());
        versionMapper.insert(version);
        return version;
    }

    @Override
    public String getDraft(String appCode) {
        LambdaQueryWrapper<AgentAppDraft> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentAppDraft::getAppCode, appCode);
        AgentAppDraft draft = draftMapper.selectOne(wrapper);
        return draft != null ? draft.getDraftData() : null;
    }

    @Override
    public void saveDraft(String appCode, String draftData) {
        LambdaQueryWrapper<AgentAppDraft> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentAppDraft::getAppCode, appCode);
        AgentAppDraft draft = draftMapper.selectOne(wrapper);
        if (draft == null) {
            draft = new AgentAppDraft();
            draft.setAppCode(appCode);
            draft.setDraftData(draftData);
            draft.setVersion(1);
            draft.setGmtCreate(LocalDateTime.now());
            draft.setGmtModified(LocalDateTime.now());
            draftMapper.insert(draft);
        } else {
            draft.setDraftData(draftData);
            draft.setVersion(draft.getVersion() + 1);
            draft.setGmtModified(LocalDateTime.now());
            draftMapper.updateById(draft);
        }
    }

    @Override
    public void incrementVisitCount(String instanceCode) {
    }

    private AgentAppDto toDto(AgentApp app) {
        if (app == null) return null;
        AgentAppDto dto = new AgentAppDto();
        BeanUtils.copyProperties(app, dto);
        dto.setGmtCreate(app.getGmtCreate() != null ? app.getGmtCreate().format(DF) : null);
        dto.setGmtModified(app.getGmtModified() != null ? app.getGmtModified().format(DF) : null);
        return dto;
    }

    private AgentApp toEntity(AgentAppReq req, ObjectMapper objectMapper) {
        AgentApp app = new AgentApp();
        app.setAppCode(req.getAppCode());
        app.setAppName(req.getAppName());
        app.setDescription(req.getDescription());
        app.setIconUrl(req.getIconUrl());
        app.setPrompt(req.getPrompt());
        app.setModelName(req.getModelName());
        app.setModelProvider(req.getModelProvider());
        app.setStatus(req.getStatus());
        if (req.getTools() != null) {
            try { app.setTools(objectMapper.writeValueAsString(req.getTools())); } catch (Exception e) {}
        }
        if (req.getKnowledgeIds() != null) {
            try { app.setKnowledgeIds(objectMapper.writeValueAsString(req.getKnowledgeIds())); } catch (Exception e) {}
        }
        if (req.getSkillCodes() != null) {
            try { app.setSkillCodes(objectMapper.writeValueAsString(req.getSkillCodes())); } catch (Exception e) {}
        }
        if (req.getVariables() != null) {
            try { app.setVariables(objectMapper.writeValueAsString(req.getVariables())); } catch (Exception e) {}
        }
        return app;
    }
}
