package com.zifang.z.task.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zifang.z.task.core.entity.Board;
import org.apache.ibatis.annotations.Mapper;

/**
 * 看板表 Mapper
 *
 * @author zifang
 */
@Mapper
public interface BoardMapper extends BaseMapper<Board> {
}
