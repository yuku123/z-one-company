package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.TenantBizService;
import com.zifang.ctc.core.service.dto.TenantDTO;
import com.zifang.z.ctc.web.api.request.TenantReq;
import com.zifang.z.ctc.web.api.response.TenantResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "租户管理")
@RestController
@RequestMapping("/api/tenant")
public class TenantManagerController {

    @Resource
    private TenantBizService tenantBizService;

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public IPage<TenantResp> page(@RequestBody TenantReq req) {
        TenantDTO dto = toDto(req);
        return tenantBizService.page(dto).convert(this::toResp);
    }

    @Operation(summary = "根据租户编码查询")
    @GetMapping("/code/{tenantCode}")
    public TenantResp getByTenantCode(@PathVariable String tenantCode) {
        return toResp(tenantBizService.getByTenantCode(tenantCode));
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/{id}")
    public TenantResp getById(@PathVariable Long id) {
        return toResp(tenantBizService.getById(id));
    }

    @Operation(summary = "新增")
    @PostMapping
    public void add(@RequestBody TenantReq req) {
        tenantBizService.add(toDto(req));
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    
    public void update(@RequestBody TenantReq req) {
        tenantBizService.update(toDto(req));
    }

    @Operation(summary = "删除")
    @PostMapping("/{id}/delete")
    public void delete(@PathVariable Long id) {
        tenantBizService.delete(id);
    }

    private TenantResp toResp(TenantDTO dto) {
        if (dto == null) return null;
        TenantResp resp = new TenantResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private TenantDTO toDto(TenantReq req) {
        TenantDTO dto = new TenantDTO();
        BeanUtils.copyProperties(req, dto);
        return dto;
    }
}
