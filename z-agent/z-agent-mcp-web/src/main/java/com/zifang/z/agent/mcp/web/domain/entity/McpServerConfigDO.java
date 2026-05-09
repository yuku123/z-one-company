package com.zifang.z.agent.mcp.web.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * MCP 服务配置（持久化到 DB 的管理表）
 */
@Data
@TableName("z_mcp_server")
public class McpServerConfigDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 服务名称（唯一标识） */
    private String serverName;

    /** 传输类型: HTTP / STDIO */
    private String transportType;

    /** HTTP 端点 URL（HTTP 模式） */
    private String url;

    /** 启动命令（STDIO 模式） */
    private String command;

    /** 命令参数（JSON 数组字符串） */
    private String args;

    /** 认证 Token */
    private String authToken;

    /** 超时时间（秒） */
    private Integer timeout;

    /** 状态: active / inactive */
    private String status;

    /** 租户编码 */
    private String tenantCode;

    /** 域编码 */
    private String domainCode;

    /** 备注 */
    private String remark;

    private LocalDateTime gmtCreate;
    private LocalDateTime gmtUpdate;
}
