package com.nofirst.ai.code.review.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 代码审查信息表
 * </p>
 *
 * @author nofirst
 * @date 2025-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("review_result_info_detail")
public class ReviewResultInfoDetail extends Model<ReviewResultInfoDetail> {


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * review_result_info 表的主键
     */
    @TableField("result_id")
    private Long resultId;

    /**
     * review维度
     */
    private String dimension;

    /**
     * review评分
     */
    @TableField("review_score")
    private Integer reviewScore;

    /**
     * 该项满分
     */
    @TableField("full_score")
    private Integer fullScore;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;


}
