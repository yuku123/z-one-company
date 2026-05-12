package com.zifang.z.task.core.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.task.core.common.exception.BusinessException;
import com.zifang.z.task.core.common.result.ResultCode;
import com.zifang.z.task.core.entity.Project;
import com.zifang.z.task.core.entity.ProjectMember;
import com.zifang.z.task.core.mapper.ProjectMapper;
import com.zifang.z.task.core.domain.service.IProjectDomainService;
import com.zifang.z.task.core.service.ProjectMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectDomainServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectDomainService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProjectMemberService projectMemberService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Project createProject(Project project, String ownerId) {
        project.setOwnerId(ownerId);
        project.setStatus(1);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        this.save(project);

        ProjectMember member = new ProjectMember();
        member.setProjectId(project.getId());
        member.setUserId(ownerId);
        member.setRole(0);
        member.setJoinedAt(LocalDateTime.now());
        projectMemberService.save(member);

        log.info("创建项目成功: projectId={}, name={}, ownerId={}", project.getId(), project.getName(), ownerId);
        return project;
    }

    @Override
    public List<Project> getUserProjects(String userId) {
        List<Long> projectIds = projectMemberService.getProjectIdsByUserId(userId);
        if (projectIds.isEmpty()) {
            return null;
        }
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Project::getId, projectIds);
        wrapper.eq(Project::getStatus, 1);
        wrapper.orderByDesc(Project::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean archiveProject(Long projectId, String userId) {
        Project project = this.getById(projectId);
        if (project == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "项目不存在");
        }
        if (!project.getOwnerId().equals(userId)) {
            ProjectMember member = projectMemberService.getMember(projectId, userId);
            if (member == null || (member.getRole() != 0 && member.getRole() != 1)) {
                throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权限归档该项目");
            }
        }
        project.setStatus(0);
        project.setUpdatedAt(LocalDateTime.now());
        boolean success = this.updateById(project);
        if (success) {
            log.info("归档项目成功: projectId={}, operatorId={}", projectId, userId);
        }
        return success;
    }

    @Override
    public boolean isProjectMember(Long projectId, String userId) {
        return projectMemberService.isMember(projectId, userId);
    }
}
