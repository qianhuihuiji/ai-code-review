package com.nofirst.ai.code.review.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("review_result_info")
public class ReviewResultInfo {


    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 事件类型，如 push、merge_request等
     */
    @TableField("object_kind")
    private String objectKind;


    /**
     * gitlab 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * gitlab 用户名
     */
    @TableField("user_username")
    private String username;

    /**
     * 事件原始信息，json格式
     */
    @TableField("origin_info")
    private String originInfo;

    /**
     * review结果，markdown格式
     */
    @TableField("review_result")
    private String reviewResult;

    /**
     * Review 评分
     */
    @TableField("review_score")
    private Integer reviewScore;

    /**
     * Review 状态，0未开始，1成功，2失败
     */
    @TableField("review_status")
    private Integer reviewStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 关联配置表id
     */
    @TableField("config_id")
    private Long configId;

    /**
     * 关联工程名称
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 失败时的错误内容
     */
    @TableField("failure_msg")
    private String failureMsg;
}
