package com.nofirst.ai.code.review.repository.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nofirst.ai.code.review.repository.dao.IReviewProjectTaskDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewProjectTask;
import com.nofirst.ai.code.review.repository.mapper.ReviewProjectTaskMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 代码审查信息表 服务实现类
 * </p>
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Service
public class ReviewProjectTaskDAOImpl extends ServiceImpl<ReviewProjectTaskMapper, ReviewProjectTask> implements IReviewProjectTaskDAO {

}
