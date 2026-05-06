package com.zifang.z.config.web.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zifang.util.core.meta.Result;
import com.zifang.z.config.core.domain.entity.ZCluster;
import com.zifang.z.config.core.domain.service.IZClusterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cluster")
@Tag(name = "008_集群(命名空间)管理")
public class ClusterController {

    @Resource
    private IZClusterService clusterService;

    @GetMapping("/list")
    @Operation(summary = "001_获取所有集群(命名空间)列表")
    public Result<List<ZCluster>> list() {
        QueryWrapper<ZCluster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        return Result.success(clusterService.list(queryWrapper));
    }

    @PostMapping("/save")
    @Operation(summary = "002_新增/更新集群(命名空间)")
    public Result<String> save(@RequestBody ZCluster cluster) {
        if (cluster.getId() == null) {
            cluster.setGmtCreate(LocalDateTime.now());
            cluster.setGmtModified(LocalDateTime.now());
            clusterService.save(cluster);
        } else {
            cluster.setGmtModified(LocalDateTime.now());
            clusterService.updateById(cluster);
        }
        return Result.success();
    }

    @PostMapping("/{id}/delete")
    @Operation(summary = "003_删除集群(命名空间)")
    public Result<String> delete(@PathVariable Long id) {
        clusterService.removeById(id);
        return Result.success();
    }
}
