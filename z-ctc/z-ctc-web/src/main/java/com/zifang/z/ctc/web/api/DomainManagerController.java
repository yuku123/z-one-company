package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zifang.ctc.core.domain.entity.DomainDO;
import com.zifang.ctc.core.service.DomainService;
import com.zifang.z.ctc.web.api.request.DomainReq;
import com.zifang.z.ctc.web.api.response.DomainResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/domain")
@Tag(name = "002_域管理")
public class DomainManagerController {

    @Resource
    private DomainService domainService;

    @GetMapping("/list")
    @Operation(summary = "列表")
    public List<DomainResp> list() {
        return domainService.list().stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/page")
    @Operation(summary = "分页列表")
    public IPage<DomainResp> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize,
            DomainReq req) {
        DomainDO entity = toEntity(req);
        IPage<DomainDO> page = domainService.page(new Page<>(current, pageSize), entity);
        return page.convert(this::toResp);
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "根据租户ID查询域列表")
    public List<DomainResp> listByTenantId(@PathVariable Long tenantId) {
        return domainService.listByTenantId(tenantId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @GetMapping("/code/{domainCode}")
    @Operation(summary = "根据域编码查询")
    public DomainResp getByDomainCode(@PathVariable String domainCode) {
        return toResp(domainService.getByDomainCode(domainCode));
    }

    @GetMapping("/{id}")
    @Operation(summary = "详情")
    public DomainResp getById(@PathVariable Long id) {
        return toResp(domainService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增")
    public boolean add(@RequestBody DomainReq req) {
        return domainService.add(toEntity(req));
    }

    @PutMapping
    @Operation(summary = "更新")
    public boolean update(@RequestBody DomainReq req) {
        return domainService.update(toEntity(req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public boolean delete(@PathVariable Long id) {
        return domainService.delete(id);
    }

    private DomainResp toResp(DomainDO entity) {
        if (entity == null) return null;
        DomainResp resp = new DomainResp();
        BeanUtils.copyProperties(entity, resp);
        return resp;
    }

    private DomainDO toEntity(DomainReq req) {
        if (req == null) return null;
        DomainDO entity = new DomainDO();
        BeanUtils.copyProperties(req, entity);
        return entity;
    }
}
