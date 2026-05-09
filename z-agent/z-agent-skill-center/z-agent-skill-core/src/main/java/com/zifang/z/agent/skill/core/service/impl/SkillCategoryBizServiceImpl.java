package com.zifang.z.agent.skill.core.service.impl;

import com.zifang.z.agent.skill.core.domain.entity.SkillCategory;
import com.zifang.z.agent.skill.core.domain.service.ISkillCategoryService;
import com.zifang.z.agent.skill.core.service.SkillCategoryBizService;
import com.zifang.z.agent.skill.core.service.dto.SkillCategoryDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillCategoryBizServiceImpl implements SkillCategoryBizService {

    @Autowired
    private ISkillCategoryService skillCategoryService;

    @Override
    public List<SkillCategoryDTO> tree() {
        List<SkillCategory> all = skillCategoryService.listAll();

        List<SkillCategory> sorted = all.stream()
                .sorted(Comparator.comparing(SkillCategory::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        List<SkillCategory> roots = sorted.stream()
                .filter(c -> c.getParentCode() == null || c.getParentCode().isEmpty())
                .collect(Collectors.toList());

        List<SkillCategoryDTO> result = new ArrayList<>();
        for (SkillCategory root : roots) {
            SkillCategoryDTO rootDTO = toDTO(root);
            buildChildren(rootDTO, sorted);
            result.add(rootDTO);
        }
        return result;
    }

    private void buildChildren(SkillCategoryDTO parent, List<SkillCategory> all) {
        List<SkillCategoryDTO> children = all.stream()
                .filter(c -> parent.getCatCode().equals(c.getParentCode()))
                .map(this::toDTO)
                .collect(Collectors.toList());
        for (SkillCategoryDTO child : children) {
            buildChildren(child, all);
        }
        parent.setChildren(children);
    }

    @Override
    public SkillCategoryDTO create(SkillCategoryDTO dto) {
        SkillCategory entity = toEntity(dto);
        entity.setGmtCreate(LocalDateTime.now());
        skillCategoryService.save(entity);
        return toDTO(entity);
    }

    @Override
    public void delete(Long id) {
        skillCategoryService.removeById(id);
    }

    private SkillCategoryDTO toDTO(SkillCategory entity) {
        SkillCategoryDTO dto = new SkillCategoryDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setChildren(new ArrayList<>());
        return dto;
    }

    private SkillCategory toEntity(SkillCategoryDTO dto) {
        SkillCategory entity = new SkillCategory();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

}
