package com.zifang.z.task.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zifang.z.task.core.entity.ProjectMember;
import com.zifang.z.task.core.mapper.ProjectMemberMapper;
import com.zifang.z.task.core.service.ProjectMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目成员表 Service 实现
 *
 * @author zifang
 */
@Service
public class ProjectMemberServiceImpl extends ServiceImpl<ProjectMemberMapper, ProjectMember> implements ProjectMemberService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public List<Long> getProjectIdsByUserId(String userId) {
        return baseMapper.selectProjectIdsByUserId(userId);
    }

    @Override
    public ProjectMember getMember(Long projectId, String userId) {
        return baseMapper.selectByProjectAndUser(projectId, userId);
    }

    @Override
    public boolean isMember(Long projectId, String userId) {
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId);
        wrapper.eq(ProjectMember::getUserId, userId);
        return this.count(wrapper) > 0;
    }

    @Override
    public boolean addMember(Long projectId, String userId, Integer role) {
        // 检查是否已经是成员
        if (isMember(projectId, userId)) {
            log.warn("添加项目成员失败，用户已经是成员: projectId={}, userId={}", projectId, userId);
            return false;
        }

        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(userId);
        member.setRole(role);
        member.setJoinedAt(LocalDateTime.now());

        boolean success = this.save(member);
        if (success) {
            log.info("添加项目成员成功: projectId={}, userId={}, role={}", projectId, userId, role);
        }
        return success;
    }

    @Override
    public boolean removeMember(Long projectId, String userId) {
        LambdaQueryWrapper<ProjectMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMember::getProjectId, projectId);
        wrapper.eq(ProjectMember::getUserId, userId);

        boolean success = this.remove(wrapper);
        if (success) {
            log.info("移除项目成员成功: projectId={}, userId={}", projectId, userId);
        }
        return success;
    }
}
