package com.nofirst.ai.code.review.service;

import com.nofirst.ai.code.review.repository.dao.IReviewConfigInfoDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Slf4j
@Component
@Service
@AllArgsConstructor
public class ReviewConfigService {

    private final IReviewConfigInfoDAO reviewConfigInfoDAO;

    public ReviewConfigInfo fetchValidReviewConfigInfo(Long configId, String gitlabUrl, String gitlabToken) {
        ReviewConfigInfo reviewConfigInfo = reviewConfigInfoDAO.getById(configId);
        if (Objects.isNull(reviewConfigInfo)) {
            throw new RuntimeException("invalid config id:" + configId);
        }

        if (!StringUtils.hasText(reviewConfigInfo.getGitlabUrl())) {
            if (StringUtils.hasText(gitlabUrl)) {
                reviewConfigInfo.setGitlabUrl(gitlabUrl);
            } else {
                throw new RuntimeException("missing gitlab url");
            }
        }
        if (!StringUtils.hasText(reviewConfigInfo.getGitlabToken())) {
            if (StringUtils.hasText(gitlabToken)) {
                reviewConfigInfo.setGitlabToken(gitlabToken);
            } else {
                throw new RuntimeException("missing gitlab token");
            }
        }

        return reviewConfigInfo;
    }
}

