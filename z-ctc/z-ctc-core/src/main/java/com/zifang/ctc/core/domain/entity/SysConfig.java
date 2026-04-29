package com.zifang.ctc.core.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * 无注释
 */
@TableName("sys_config")
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    @TableField("variable")
    private String variable;

    /**
     *
     */
    @TableField("value")
    private String value;

    /**
     *
     */
    @TableField("set_time")
    private LocalDateTime setTime;

    /**
     *
     */
    @TableField("set_by")
    private String setBy;

    public String getVariable() {
        return variable;
    }
    public void setVariable(String variable) {
        this.variable = variable;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public LocalDateTime getSetTime() {
        return setTime;
    }
    public void setSetTime(LocalDateTime setTime) {
        this.setTime = setTime;
    }
    public String getSetBy() {
        return setBy;
    }
    public void setSetBy(String setBy) {
        this.setBy = setBy;
    }
}
