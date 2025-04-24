package com.nofirst.ai.code.review.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("review_config_info")
public class ReviewConfigInfo {


    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 项目名称
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 项目描述
     */
    @TableField("project_description")
    private String projectDescription;


    /**
     * gitlab url
     */
    @TableField("gitlab_url")
    private String gitlabUrl;

    /**
     * gitlab token
     */
    @TableField("gitlab_token")
    private String gitlabToken;

    /**
     * 钉钉机器人webhook地址
     */
    @TableField("ding_webhook_url")
    private String dingWebhookUrl;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

}
