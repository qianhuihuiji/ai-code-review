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
@TableName("review_project_task")
public class ReviewProjectTask {


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

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
     * 已review的文件数量
     */
    @TableField("review_file_count")
    private Integer reviewFileCount;

    /**
     * 总共需要review的文件数量
     */
    @TableField("total_file_count")
    private Integer totalFileCount;

    /**
     * 路径
     */
    @TableField("path")
    private String path;

    /**
     * 文件路径
     */
    @TableField("ref")
    private String ref;
}
