package com.zifang.z.agent.center.core.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.agent.center.core.app.entity.AgentApp;
import com.zifang.z.agent.center.core.app.entity.AgentAppDraft;
import com.zifang.z.agent.center.core.app.entity.AgentAppVersion;
import com.zifang.z.agent.center.core.app.mapper.AgentAppDraftMapper;
import com.zifang.z.agent.center.core.app.mapper.AgentAppMapper;
import com.zifang.z.agent.center.core.app.mapper.AgentAppVersionMapper;
import com.zifang.z.agent.center.core.app.service.AgentAppService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AgentAppServiceImpl extends ServiceImpl<AgentAppMapper, AgentApp> implements AgentAppService {

    @Resource
    private AgentAppVersionMapper versionMapper;

    @Resource
    private AgentAppDraftMapper draftMapper;

    @Override
    public IPage<AgentApp> page(String keyword, String status, int pageNum, int pageSize) {
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
        return this.page(new Page<>(pageNum, pageSize), wrapper);
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
    public void delete(Long id) {
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
        // 由AgentInstanceService.recordVisit处理
    }
}
