package com.nofirst.ai.code.review.repository.dao.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nofirst.ai.code.review.repository.dao.IReviewEventTaskDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewEventTask;
import com.nofirst.ai.code.review.repository.mapper.ReviewEventTaskMapper;
import org.springframework.stereotype.Service;


@Service
public class ReviewEventTaskDAOImpl extends ServiceImpl<ReviewEventTaskMapper, ReviewEventTask>
        implements IReviewEventTaskDAO {

}
