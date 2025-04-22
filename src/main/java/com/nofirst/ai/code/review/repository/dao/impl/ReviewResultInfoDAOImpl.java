package com.nofirst.ai.code.review.repository.dao.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nofirst.ai.code.review.repository.dao.IReviewResultInfoDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfo;
import com.nofirst.ai.code.review.repository.mapper.ReviewResultInfoMapper;
import org.springframework.stereotype.Service;


@Service
public class ReviewResultInfoDAOImpl extends ServiceImpl<ReviewResultInfoMapper, ReviewResultInfo>
        implements IReviewResultInfoDAO {

}
