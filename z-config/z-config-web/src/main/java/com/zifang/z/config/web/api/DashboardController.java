package com.zifang.z.config.web.api;

import com.zifang.z.config.core.domain.mapper.ZClusterMapper;
import com.zifang.z.config.core.domain.mapper.ZConfigInfoMapper;
import com.zifang.z.config.core.domain.mapper.ZInstanceMapper;
import com.zifang.z.config.core.domain.mapper.ZServiceInfoMapper;
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
    private ZConfigInfoMapper configInfoMapper;

    @Resource
    private ZServiceInfoMapper serviceInfoMapper;

    @Resource
    private ZInstanceMapper instanceMapper;

    @Resource
    private ZClusterMapper clusterMapper;

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        // 配置总数
        stats.put("configCount", configInfoMapper.selectCount(null).longValue());
        // 服务总数
        stats.put("serviceCount", serviceInfoMapper.selectCount(null).longValue());
        // 实例总数
        stats.put("instanceCount", instanceMapper.selectCount(null).longValue());
        // 命名空间（集群）总数
        stats.put("namespaceCount", clusterMapper.selectCount(null).longValue());
        return stats;
    }
}
