package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.OrgDO;
import com.zifang.ctc.core.service.OrgService;
import com.zifang.z.ctc.web.api.request.OrgReq;
import com.zifang.z.ctc.web.api.response.OrgResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/org")
@Tag(name = "003_组织管理")
public class OrgManagerController {

    @Resource
    private OrgService orgService;

    @GetMapping("/list")
    @Operation(summary = "列表")
    public List<OrgResp> list() {
        return orgService.list().stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/page")
    @Operation(summary = "分页列表")
    public IPage<OrgResp> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize,
            OrgReq req) {
        OrgDO entity = toEntity(req);
        IPage<OrgDO> page = orgService.page(new Page<>(current, pageSize), entity);
        return page.convert(this::toResp);
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "根据租户ID查询组织列表")
    public List<OrgResp> listByTenantId(@PathVariable Long tenantId) {
        return orgService.listByTenantId(tenantId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/domain/{domainId}")
    @Operation(summary = "根据域ID查询组织列表")
    public List<OrgResp> listByDomainId(@PathVariable Long domainId) {
        return orgService.listByDomainId(domainId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "详情")
    public OrgResp getById(@PathVariable Long id) {
        return toResp(orgService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增")
    public boolean add(@RequestBody OrgReq req) {
        return orgService.add(toEntity(req));
    }

    @PutMapping
    @Operation(summary = "更新")
    public boolean update(@RequestBody OrgReq req) {
        return orgService.update(toEntity(req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public boolean delete(@PathVariable Long id) {
        return orgService.delete(id);
    }

    private OrgResp toResp(OrgDO entity) {
        if (entity == null) return null;
        OrgResp resp = new OrgResp();
        BeanUtils.copyProperties(entity, resp);
        return resp;
    }

    private OrgDO toEntity(OrgReq req) {
        if (req == null) return null;
        OrgDO entity = new OrgDO();
        BeanUtils.copyProperties(req, entity);
        return entity;
    }
}
