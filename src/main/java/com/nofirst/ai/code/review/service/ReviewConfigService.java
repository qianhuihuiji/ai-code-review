package com.nofirst.ai.code.review.service;

import com.nofirst.ai.code.review.repository.dao.IReviewConfigInfoDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * The type Review config service.
 */
@Slf4j
@Component
@Service
@AllArgsConstructor
public class ReviewConfigService {

    private final IReviewConfigInfoDAO reviewConfigInfoDAO;

    /**
     * 获取到有效的配置信息
     *
     * @param configId    the config id
     * @param gitlabUrl   webhook 请求头附带的 gitlabUrl，如果有值，优先使用
     * @param gitlabToken webhook 请求头附带的 gitlabToken，如果有值，优先使用
     * @return the review config info
     */
    public ReviewConfigInfo fetchValidReviewConfigInfo(Long configId, String gitlabUrl, String gitlabToken) {
        ReviewConfigInfo reviewConfigInfo = reviewConfigInfoDAO.getById(configId);
        if (Objects.isNull(reviewConfigInfo)) {
            throw new RuntimeException("invalid config id:" + configId);
        }
        if (StringUtils.hasText(gitlabUrl)) {
            reviewConfigInfo.setGitlabUrl(gitlabUrl);
        }
        if (StringUtils.hasText(gitlabToken)) {
            reviewConfigInfo.setGitlabToken(gitlabToken);
        }

        if (!StringUtils.hasText(reviewConfigInfo.getGitlabUrl())) {
            throw new RuntimeException("missing gitlab url");
        }
        if (!StringUtils.hasText(reviewConfigInfo.getGitlabToken())) {
            throw new RuntimeException("missing gitlab token");
        }

        return reviewConfigInfo;
    }
}

