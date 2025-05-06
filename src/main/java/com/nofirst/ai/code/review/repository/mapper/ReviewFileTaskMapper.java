package com.nofirst.ai.code.review.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nofirst.ai.code.review.repository.entity.ReviewFileTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 代码审查信息表 Mapper 接口
 * </p>
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Mapper
public interface ReviewFileTaskMapper extends BaseMapper<ReviewFileTask> {

}
