package com.zifang.z.ctc.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zifang.ctc.core.service.DomainBizService;
import com.zifang.ctc.core.service.dto.DomainDTO;
import com.zifang.z.ctc.web.api.request.DomainReq;
import com.zifang.z.ctc.web.api.response.DomainResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "域管理")
@RestController
@RequestMapping("/api/domain")
public class DomainManagerController {

    @Resource
    private DomainBizService domainBizService;

    @Operation(summary = "列表")
    @GetMapping("/list")
    public List<DomainResp> list() {
        return domainBizService.list().stream().map(this::toResp).collect(Collectors.toList());
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public IPage<DomainResp> page(@RequestBody DomainReq req) {
        return domainBizService.page(toDto(req)).convert(this::toResp);
    }

    @Operation(summary = "根据租户ID查询")
    @GetMapping("/tenant/{tenantId}")
    public List<DomainResp> listByTenantId(@PathVariable Long tenantId) {
        return domainBizService.listByTenantId(tenantId).stream().map(this::toResp).collect(Collectors.toList());
    }

    @Operation(summary = "根据域编码查询")
    @GetMapping("/code/{domainCode}")
    public DomainResp getByDomainCode(@PathVariable String domainCode) {
        return toResp(domainBizService.getByDomainCode(domainCode));
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/{id}")
    public DomainResp getById(@PathVariable Long id) {
        return toResp(domainBizService.getById(id));
    }

    @Operation(summary = "新增")
    @PostMapping
    public void add(@RequestBody DomainReq req) {
        domainBizService.add(toDto(req));
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    
    public void update(@RequestBody DomainReq req) {
        domainBizService.update(toDto(req));
    }

    @Operation(summary = "删除")
    @PostMapping("/{id}/delete")
    public void delete(@PathVariable Long id) {
        domainBizService.delete(id);
    }

    private DomainResp toResp(DomainDTO dto) {
        if (dto == null) return null;
        DomainResp resp = new DomainResp();
        BeanUtils.copyProperties(dto, resp);
        return resp;
    }

    private DomainDTO toDto(DomainReq req) {
        DomainDTO dto = new DomainDTO();
        BeanUtils.copyProperties(req, dto);
        return dto;
    }
}
