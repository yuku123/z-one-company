package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.OrgBizService;
import com.zifang.ctc.core.service.dto.OrgDTO;
import com.zifang.z.ctc.web.api.request.OrgReq;
import com.zifang.z.ctc.web.api.response.OrgResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "组织管理")
@RestController
@RequestMapping("/api/org")
public class OrgManagerController {

    @Resource
    private OrgBizService orgBizService;

    @Operation(summary = "列表")
    @GetMapping("/list")
    public List<OrgResp> list() {
        return orgBizService.list().stream().map(this::toResp).collect(Collectors.toList());
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public IPage<OrgResp> page(@RequestBody OrgReq req) {
        return orgBizService.page(toDto(req)).convert(this::toResp);
    }

    @Operation(summary = "根据租户ID查询")
    @GetMapping("/tenant/{tenantId}")
    public List<OrgResp> listByTenantId(@PathVariable Long tenantId) {
        return orgBizService.listByTenantId(tenantId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @Operation(summary = "根据域ID查询")
    @GetMapping("/domain/{domainId}")
    public List<OrgResp> listByDomainId(@PathVariable Long domainId) {
        return orgBizService.listByDomainId(domainId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/{id}")
    public OrgResp getById(@PathVariable Long id) {
        return toResp(orgBizService.getById(id));
    }

    @Operation(summary = "新增")
    @PostMapping
    public void add(@RequestBody OrgReq req) {
        orgBizService.add(toDto(req));
    }

    @Operation(summary = "更新")
    @PutMapping
    public void update(@RequestBody OrgReq req) {
        orgBizService.update(toDto(req));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orgBizService.delete(id);
    }

    private OrgResp toResp(OrgDTO dto) {
        if (dto == null) return null;
        OrgResp resp = new OrgResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private OrgDTO toDto(OrgReq req) {
        OrgDTO dto = new OrgDTO();
        BeanUtils.copyProperties(req, dto);
        return dto;
    }
}
