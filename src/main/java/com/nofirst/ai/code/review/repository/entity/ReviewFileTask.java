package com.nofirst.ai.code.review.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 代码审查信息表
 * </p>
 */
@Data
@TableName("review_file_task")
public class ReviewFileTask {


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * review结果，markdown格式
     */
    @TableField("review_result")
    private String reviewResult;

    /**
     * review评分
     */
    @TableField("review_score")
    private Integer reviewScore;

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
     * 状态，0未开始，1成功，2失败
     */
    @TableField("review_status")
    private Integer reviewStatus;

    /**
     * review_project_task 表的主键，如果为空，则说明是直接对文件进行的review
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件路径
     */
    @TableField("ref")
    private String ref;

}
