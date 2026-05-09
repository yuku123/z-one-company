package com.zifang.z.agent.skill.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.util.core.meta.Result;
import com.zifang.z.agent.skill.core.service.SkillBizService;
import com.zifang.z.agent.skill.core.service.SkillCategoryBizService;
import com.zifang.z.agent.skill.core.service.dto.SkillCategoryDTO;
import com.zifang.z.agent.skill.core.service.dto.SkillDTO;
import com.zifang.z.agent.skill.core.service.dto.SkillVersionDTO;
import com.zifang.z.agent.skill.web.api.request.SkillCategoryReq;
import com.zifang.z.agent.skill.web.api.request.SkillInstallReq;
import com.zifang.z.agent.skill.web.api.request.SkillPageReq;
import com.zifang.z.agent.skill.web.api.request.SkillReq;
import com.zifang.z.agent.skill.web.api.request.SkillVersionReq;
import com.zifang.z.agent.skill.web.api.response.SkillCategoryResp;
import com.zifang.z.agent.skill.web.api.response.SkillResp;
import com.zifang.z.agent.skill.web.api.response.SkillVersionResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "技能市场")
@RestController
@RequestMapping("/api/skill")
public class SkillController {

    @Resource
    private SkillBizService skillBizService;

    @Resource
    private SkillCategoryBizService skillCategoryBizService;

    // ==================== 技能接口 ====================

    @Operation(summary = "分页查询技能")
    @PostMapping("/page")
    public Result<IPage<SkillResp>> page(@RequestBody SkillPageReq req) {
        SkillDTO query = new SkillDTO();
        query.setSkillName(req.getKeyword());
        query.setCategoryCode(req.getCategoryCode());
        query.setTags(req.getTags());
        query.setStatus(req.getStatus());
        int pageNum = req.getCurrent() != null ? req.getCurrent().intValue() : 1;
        int pageSize = req.getSize() != null ? req.getSize().intValue() : 12;
        IPage<SkillDTO> page = skillBizService.page(query, pageNum, pageSize);
        return Result.success(page.convert(this::toResp));
    }

    @Operation(summary = "根据技能编码查询")
    @GetMapping("/get")
    public Result<SkillResp> get(@RequestParam String skillCode) {
        SkillDTO dto = skillBizService.getBySkillCode(skillCode);
        return Result.success(toResp(dto));
    }

    @Operation(summary = "获取技能内容")
    @GetMapping("/content")
    public Result<String> content(@RequestParam String skillCode) {
        SkillDTO dto = skillBizService.getBySkillCode(skillCode);
        return Result.success(dto != null ? dto.getContent() : null);
    }

    @Operation(summary = "新增技能")
    @PostMapping
    public Result<SkillResp> create(@RequestBody SkillReq req) {
        SkillDTO dto = toDto(req);
        SkillDTO created = skillBizService.create(dto);
        return Result.success(toResp(created));
    }

    @Operation(summary = "更新技能")
    @PostMapping("/update")
    public Result<SkillResp> update(@RequestBody SkillReq req) {
        SkillDTO dto = toDto(req);
        SkillDTO updated = skillBizService.update(dto);
        return Result.success(toResp(updated));
    }

    @Operation(summary = "删除技能")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        skillBizService.delete(id);
        return Result.success();
    }

    @Operation(summary = "发布技能")
    @PostMapping("/publish")
    public Result<Void> publish(@RequestBody SkillReq req) {
        skillBizService.publish(req.getSkillCode());
        return Result.success();
    }

    @Operation(summary = "安装技能")
    @PostMapping("/install")
    public Result<Void> install(@RequestBody SkillInstallReq req) {
        skillBizService.install(req.getSkillCode(), req.getInstalledBy(), req.getTenantCode());
        return Result.success();
    }

    @Operation(summary = "查询技能版本列表")
    @GetMapping("/versions")
    public Result<List<SkillVersionResp>> versions(@RequestParam String skillCode) {
        List<SkillVersionDTO> list = skillBizService.versions(skillCode);
        List<SkillVersionResp> data = list.stream().map(this::toVersionResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "新增技能版本")
    @PostMapping("/version")
    public Result<SkillVersionResp> addVersion(@RequestBody SkillVersionReq req) {
        SkillVersionDTO dto = new SkillVersionDTO();
        BeanUtils.copyProperties(req, dto);
        SkillVersionDTO created = skillBizService.addVersion(dto);
        return Result.success(toVersionResp(created));
    }

    @Operation(summary = "热门技能")
    @GetMapping("/hot")
    public Result<List<SkillResp>> hot(@RequestParam(defaultValue = "10") int limit) {
        List<SkillDTO> list = skillBizService.hot(limit);
        List<SkillResp> data = list.stream().map(this::toResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "技能统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> map = skillBizService.stats();
        return Result.success(map);
    }

    // ==================== 分类接口 ====================

    @Operation(summary = "分类树")
    @GetMapping("/category/tree")
    public Result<List<SkillCategoryResp>> categoryTree() {
        List<SkillCategoryDTO> list = skillCategoryBizService.tree();
        List<SkillCategoryResp> data = list.stream().map(this::toCategoryResp).collect(Collectors.toList());
        return Result.success(data);
    }

    @Operation(summary = "新增分类")
    @PostMapping("/category")
    public Result<SkillCategoryResp> createCategory(@RequestBody SkillCategoryReq req) {
        SkillCategoryDTO dto = new SkillCategoryDTO();
        BeanUtils.copyProperties(req, dto);
        SkillCategoryDTO created = skillCategoryBizService.create(dto);
        return Result.success(toCategoryResp(created));
    }

    @Operation(summary = "删除分类")
    @PostMapping("/category/{id}/delete")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        skillCategoryBizService.delete(id);
        return Result.success();
    }

    // ==================== 私有转换方法 ====================

    private SkillResp toResp(SkillDTO dto) {
        if (dto == null) {
            return null;
        }
        SkillResp resp = new SkillResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private SkillDTO toDto(SkillReq req) {
        if (req == null) {
            return null;
        }
        SkillDTO dto = new SkillDTO();
        BeanUtils.copyProperties(req, dto);
        return dto;
    }

    private SkillVersionResp toVersionResp(SkillVersionDTO dto) {
        if (dto == null) {
            return null;
        }
        SkillVersionResp resp = new SkillVersionResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private SkillCategoryResp toCategoryResp(SkillCategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        SkillCategoryResp resp = new SkillCategoryResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }
}
