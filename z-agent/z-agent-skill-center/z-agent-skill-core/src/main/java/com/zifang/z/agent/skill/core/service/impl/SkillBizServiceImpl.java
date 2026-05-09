package com.zifang.z.agent.skill.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.z.agent.skill.core.domain.entity.Skill;
import com.zifang.z.agent.skill.core.domain.entity.SkillInstall;
import com.zifang.z.agent.skill.core.domain.entity.SkillVersion;
import com.zifang.z.agent.skill.core.domain.service.ISkillInstallService;
import com.zifang.z.agent.skill.core.domain.service.ISkillService;
import com.zifang.z.agent.skill.core.domain.service.ISkillVersionService;
import com.zifang.z.agent.skill.core.service.SkillBizService;
import com.zifang.z.agent.skill.core.service.dto.SkillDTO;
import com.zifang.z.agent.skill.core.service.dto.SkillVersionDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillBizServiceImpl implements SkillBizService {

    @Autowired
    private ISkillService skillService;

    @Autowired
    private ISkillVersionService skillVersionService;

    @Autowired
    private ISkillInstallService skillInstallService;

    @Override
    public IPage<SkillDTO> page(SkillDTO query, int pageNum, int pageSize) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();

        if (query != null) {
            if (StringUtils.isNotBlank(query.getSkillName())) {
                wrapper.and(w -> w
                        .like(Skill::getSkillName, query.getSkillName())
                        .or()
                        .like(Skill::getDescription, query.getSkillName()));
            }
            if (StringUtils.isNotBlank(query.getCategoryCode())) {
                wrapper.eq(Skill::getCategoryCode, query.getCategoryCode());
            }
            if (StringUtils.isNotBlank(query.getStatus())) {
                wrapper.eq(Skill::getStatus, query.getStatus());
            }
            if (StringUtils.isNotBlank(query.getTags())) {
                wrapper.like(Skill::getTags, query.getTags());
            }
        }

        wrapper.orderByDesc(Skill::getDownloadCount);

        Page<Skill> page = new Page<>(pageNum, pageSize);
        IPage<Skill> skillPage = skillService.page(page, wrapper);

        return skillPage.convert(this::toDTO);
    }

    @Override
    public SkillDTO getBySkillCode(String skillCode) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Skill::getSkillCode, skillCode);
        Skill skill = skillService.getOne(wrapper);
        return skill != null ? toDTO(skill) : null;
    }

    @Override
    public SkillDTO create(SkillDTO dto) {
        Skill skill = toEntity(dto);
        skill.setGmtCreate(LocalDateTime.now());
        skill.setStatus("DRAFT");
        if (skill.getDownloadCount() == null) {
            skill.setDownloadCount(0L);
        }
        skillService.save(skill);
        return toDTO(skill);
    }

    @Override
    public SkillDTO update(SkillDTO dto) {
        Skill existing = skillService.getById(dto.getId());
        if (existing == null) {
            return null;
        }
        copyNonNullProperties(dto, existing);
        skillService.updateById(existing);
        return toDTO(existing);
    }

    @Override
    public void delete(Long id) {
        skillService.removeById(id);
    }

    @Override
    public void publish(String skillCode) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Skill::getSkillCode, skillCode);
        Skill skill = skillService.getOne(wrapper);
        if (skill != null) {
            skill.setStatus("PUBLISHED");
            skillService.updateById(skill);
        }
    }

    @Override
    public void install(String skillCode, String installedBy, String tenantCode) {
        LambdaUpdateWrapper<Skill> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Skill::getSkillCode, skillCode);
        updateWrapper.setSql("download_count = download_count + 1");
        skillService.update(updateWrapper);

        SkillInstall install = new SkillInstall();
        install.setSkillCode(skillCode);
        install.setInstalledBy(installedBy);
        install.setTenantCode(tenantCode);
        install.setGmtCreate(LocalDateTime.now());
        skillInstallService.save(install);
    }

    @Override
    public List<SkillDTO> hot(int limit) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Skill::getDownloadCount);
        Page<Skill> page = new Page<>(1, limit);
        IPage<Skill> skillPage = skillService.page(page, wrapper);
        return skillPage.getRecords().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<SkillVersionDTO> versions(String skillCode) {
        LambdaQueryWrapper<SkillVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkillVersion::getSkillCode, skillCode);
        wrapper.orderByDesc(SkillVersion::getGmtCreate);
        List<SkillVersion> versions = skillVersionService.list(wrapper);
        return versions.stream().map(this::toVersionDTO).collect(Collectors.toList());
    }

    @Override
    public SkillVersionDTO addVersion(SkillVersionDTO dto) {
        SkillVersion version = toVersionEntity(dto);
        version.setGmtCreate(LocalDateTime.now());
        skillVersionService.save(version);

        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Skill::getSkillCode, dto.getSkillCode());
        Skill skill = skillService.getOne(wrapper);
        if (skill != null) {
            skill.setVersion(dto.getVersion());
            skillService.updateById(skill);
        }

        return toVersionDTO(version);
    }

    private SkillDTO toDTO(Skill entity) {
        SkillDTO dto = new SkillDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private Skill toEntity(SkillDTO dto) {
        Skill entity = new Skill();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    private SkillVersionDTO toVersionDTO(SkillVersion entity) {
        SkillVersionDTO dto = new SkillVersionDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private SkillVersion toVersionEntity(SkillVersionDTO dto) {
        SkillVersion entity = new SkillVersion();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    private void copyNonNullProperties(SkillDTO source, Skill target) {
        if (source.getSkillCode() != null) target.setSkillCode(source.getSkillCode());
        if (source.getSkillName() != null) target.setSkillName(source.getSkillName());
        if (source.getDescription() != null) target.setDescription(source.getDescription());
        if (source.getAuthor() != null) target.setAuthor(source.getAuthor());
        if (source.getVersion() != null) target.setVersion(source.getVersion());
        if (source.getCategoryCode() != null) target.setCategoryCode(source.getCategoryCode());
        if (source.getTags() != null) target.setTags(source.getTags());
        if (source.getIconUrl() != null) target.setIconUrl(source.getIconUrl());
        if (source.getContent() != null) target.setContent(source.getContent());
        if (source.getStatus() != null) target.setStatus(source.getStatus());
        if (source.getDownloadCount() != null) target.setDownloadCount(source.getDownloadCount());
        if (source.getTenantCode() != null) target.setTenantCode(source.getTenantCode());
        if (source.getGmtCreate() != null) target.setGmtCreate(source.getGmtCreate());
        if (source.getGmtModified() != null) target.setGmtModified(source.getGmtModified());
        if (source.getIsDeleted() != null) target.setIsDeleted(source.getIsDeleted());
    }

}
