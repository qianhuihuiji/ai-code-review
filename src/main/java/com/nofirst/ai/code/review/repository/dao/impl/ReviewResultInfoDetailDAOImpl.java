package com.nofirst.ai.code.review.repository.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDetailDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfoDetail;
import com.nofirst.ai.code.review.repository.mapper.ReviewResultInfoDetailMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 代码审查信息表 服务实现类
 * </p>
 *
 * @author nofirst
 */
@Service
public class ReviewResultInfoDetailDAOImpl extends ServiceImpl<ReviewResultInfoDetailMapper, ReviewResultInfoDetail> implements IReviewResultInfoDetailDAO {

}
