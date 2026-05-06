package com.zifang.z.config.web.api;

import com.zifang.util.core.meta.Result;
import com.zifang.z.config.core.domain.service.IZClusterService;
import com.zifang.z.config.core.domain.service.IZConfigInfoService;
import com.zifang.z.config.core.domain.service.IZInstanceService;
import com.zifang.z.config.core.domain.service.IZServiceInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Resource
    private IZConfigInfoService configInfoService;

    @Resource
    private IZServiceInfoService serviceInfoService;

    @Resource
    private IZInstanceService instanceService;

    @Resource
    private IZClusterService clusterService;

    @GetMapping("/stats")
    public Result<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("configCount", configInfoService.count());
        stats.put("serviceCount", serviceInfoService.count());
        stats.put("instanceCount", instanceService.count());
        stats.put("namespaceCount", clusterService.count());
        return Result.success(stats);
    }
}
