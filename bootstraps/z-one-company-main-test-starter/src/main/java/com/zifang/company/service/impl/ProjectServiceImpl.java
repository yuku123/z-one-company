package com.zifang.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.task.core.common.exception.BusinessException;
import com.zifang.z.task.core.common.result.ResultCode;
import com.zifang.z.task.core.entity.Project;
import com.zifang.z.task.core.entity.ProjectMember;
import com.zifang.z.task.core.mapper.ProjectMapper;
import com.zifang.z.task.core.service.ProjectMemberService;
import com.zifang.z.task.core.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目表 Service 实现
 *
 * @author zifang
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProjectMemberService projectMemberService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Project createProject(Project project, String ownerId) {
        // 设置项目所有者
        project.setOwnerId(ownerId);
        project.setStatus(1); // 正常状态
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        // 保存项目
        this.save(project);

        // 添加项目所有者成员记录
        ProjectMember member = new ProjectMember();
        member.setProjectId(project.getId());
        member.setUserId(ownerId);
        member.setRole(0); // 所有者
        member.setJoinedAt(LocalDateTime.now());
        projectMemberService.save(member);

        log.info("创建项目成功: projectId={}, name={}, ownerId={}", project.getId(), project.getName(), ownerId);
        return project;
    }

    @Override
    public List<Project> getUserProjects(String userId) {
        // 查询用户参与的所有项目ID
        List<Long> projectIds = projectMemberService.getProjectIdsByUserId(userId);

        if (projectIds.isEmpty()) {
            return null;
        }

        // 查询项目详情
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Project::getId, projectIds);
        wrapper.eq(Project::getStatus, 1); // 只查询正常状态的项目
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

        // 检查权限（只有所有者或管理员可以归档）
        if (!project.getOwnerId().equals(userId)) {
            ProjectMember member = projectMemberService.getMember(projectId, userId);
            if (member == null || (member.getRole() != 0 && member.getRole() != 1)) {
                throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权限归档该项目");
            }
        }

        // 归档项目
        project.setStatus(0); // 归档状态
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
