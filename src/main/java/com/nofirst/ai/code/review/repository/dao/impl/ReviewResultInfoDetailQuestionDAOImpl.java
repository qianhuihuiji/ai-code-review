package com.nofirst.ai.code.review.repository.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDetailQuestionDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfoDetailIssue;
import com.nofirst.ai.code.review.repository.mapper.ReviewResultInfoDetailQuestionMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 代码审查信息表 服务实现类
 * </p>
 *
 * @author nofirst
 */
@Service
public class ReviewResultInfoDetailQuestionDAOImpl extends ServiceImpl<ReviewResultInfoDetailQuestionMapper, ReviewResultInfoDetailIssue> implements IReviewResultInfoDetailQuestionDAO {

}
