package com.zifang.z.meta.admin.api;

import com.zifang.z.meta.common.model.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查 Controller
 */
@Tag(name = "健康检查")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Operation(summary = "健康检查")
    @GetMapping
    public Result<String> health() {
        return Result.success("z-meta is running");
    }
}