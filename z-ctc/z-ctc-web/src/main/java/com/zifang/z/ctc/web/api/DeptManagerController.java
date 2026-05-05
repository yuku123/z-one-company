package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.DeptBizService;
import com.zifang.ctc.core.service.dto.DeptDTO;
import com.zifang.z.ctc.web.api.request.DeptReq;
import com.zifang.z.ctc.web.api.response.DeptResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/dept")
public class DeptManagerController {

    @Resource
    private DeptBizService deptBizService;

    @Operation(summary = "列表")
    @GetMapping("/list")
    public List<DeptResp> list() {
        return deptBizService.list().stream().map(this::toResp).collect(Collectors.toList());
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public IPage<DeptResp> page(@RequestBody DeptReq req) {
        return deptBizService.page(toDto(req)).convert(this::toResp);
    }

    @Operation(summary = "根据租户ID查询")
    @GetMapping("/tenant/{tenantId}")
    public List<DeptResp> listByTenantId(@PathVariable Long tenantId) {
        return deptBizService.listByTenantId(tenantId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @Operation(summary = "根据域ID查询")
    @GetMapping("/domain/{domainId}")
    public List<DeptResp> listByDomainId(@PathVariable Long domainId) {
        return deptBizService.listByDomainId(domainId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @Operation(summary = "根据组织ID查询")
    @GetMapping("/org/{orgId}")
    public List<DeptResp> listByOrgId(@PathVariable Long orgId) {
        return deptBizService.listByOrgId(orgId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/{id}")
    public DeptResp getById(@PathVariable Long id) {
        return toResp(deptBizService.getById(id));
    }

    @Operation(summary = "新增")
    @PostMapping
    public void add(@RequestBody DeptReq req) {
        deptBizService.add(toDto(req));
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    
    public void update(@RequestBody DeptReq req) {
        deptBizService.update(toDto(req));
    }

    @Operation(summary = "删除")
    @PostMapping("/{id}/delete")
    public void delete(@PathVariable Long id) {
        deptBizService.delete(id);
    }

    private DeptResp toResp(DeptDTO dto) {
        if (dto == null) return null;
        DeptResp resp = new DeptResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private DeptDTO toDto(DeptReq req) {
        DeptDTO dto = new DeptDTO();
        BeanUtils.copyProperties(req, dto);
        return dto;
    }
}
