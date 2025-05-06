package com.nofirst.ai.code.review.repository.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nofirst.ai.code.review.repository.dao.IReviewFileTaskDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewFileTask;
import com.nofirst.ai.code.review.repository.mapper.ReviewFileTaskMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 代码审查信息表 服务实现类
 * </p>
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Service
public class ReviewFileTaskDAOImpl extends ServiceImpl<ReviewFileTaskMapper, ReviewFileTask> implements IReviewFileTaskDAO {

}
