package com.zifang.z.task.core.service.impl;

import com.zifang.z.task.core.domain.service.IProjectDomainService;
import com.zifang.z.task.core.entity.Project;
import com.zifang.z.task.core.service.ProjectBizService;
import com.zifang.z.task.core.service.dto.ProjectDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectBizServiceImpl implements ProjectBizService {

    @Resource
    private IProjectDomainService projectDomainService;

    private ProjectDTO toDTO(Project p) {
        if (p == null) return null;
        ProjectDTO dto = new ProjectDTO();
        BeanUtils.copyProperties(p, dto);
        return dto;
    }

    private Project toDO(ProjectDTO dto) {
        if (dto == null) return null;
        Project p = new Project();
        BeanUtils.copyProperties(dto, p);
        return p;
    }

    @Override
    public List<ProjectDTO> listByUser(String userId) {
        List<Project> list = projectDomainService.getUserProjects(userId);
        if (list == null) return null;
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO create(ProjectDTO dto, String ownerId) {
        Project p = toDO(dto);
        Project created = projectDomainService.createProject(p, ownerId);
        return toDTO(created);
    }

    @Override
    public boolean archive(Long projectId, String userId) {
        return projectDomainService.archiveProject(projectId, userId);
    }

    @Override
    public boolean isMember(Long projectId, String userId) {
        return projectDomainService.isProjectMember(projectId, userId);
    }
}
