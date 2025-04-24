package com.nofirst.ai.code.review.repository.dao.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nofirst.ai.code.review.repository.dao.IReviewConfigInfoDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import com.nofirst.ai.code.review.repository.mapper.ReviewConfigInfoMapper;
import org.springframework.stereotype.Service;


@Service
public class ReviewConfigInfoDAOImpl extends ServiceImpl<ReviewConfigInfoMapper, ReviewConfigInfo>
        implements IReviewConfigInfoDAO {

}
