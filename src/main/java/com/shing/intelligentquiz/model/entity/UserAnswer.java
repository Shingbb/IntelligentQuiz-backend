package com.shing.intelligentquiz.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户答题记录
 * @TableName user_answer
 */
@TableName(value ="user_answer")
@Data
public class UserAnswer implements Serializable {

    /**
     * id(ASSIGN_ID--雪花算法)
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 应用类型（0-得分类，1-角色测评类）
     */
    private Integer appType;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    private Integer scoringStrategy;

    /**
     * 用户答案（JSON 数组）
     */
    private String choices;

    /**
     * 评分结果 id
     */
    private Long resultId;

    /**
     * 结果名称，如物流师
     */
    private String resultName;

    /**
     * 结果描述
     */
    private String resultDesc;

    /**
     * 结果图标
     */
    private String resultPicture;

    /**
     * 得分
     */
    private Integer resultScore;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        UserAnswer other = (UserAnswer) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getAppType() == null ? other.getAppType() == null : this.getAppType().equals(other.getAppType()))
            && (this.getScoringStrategy() == null ? other.getScoringStrategy() == null : this.getScoringStrategy().equals(other.getScoringStrategy()))
            && (this.getChoices() == null ? other.getChoices() == null : this.getChoices().equals(other.getChoices()))
            && (this.getResultId() == null ? other.getResultId() == null : this.getResultId().equals(other.getResultId()))
            && (this.getResultName() == null ? other.getResultName() == null : this.getResultName().equals(other.getResultName()))
            && (this.getResultDesc() == null ? other.getResultDesc() == null : this.getResultDesc().equals(other.getResultDesc()))
            && (this.getResultPicture() == null ? other.getResultPicture() == null : this.getResultPicture().equals(other.getResultPicture()))
            && (this.getResultScore() == null ? other.getResultScore() == null : this.getResultScore().equals(other.getResultScore()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getAppType() == null) ? 0 : getAppType().hashCode());
        result = prime * result + ((getScoringStrategy() == null) ? 0 : getScoringStrategy().hashCode());
        result = prime * result + ((getChoices() == null) ? 0 : getChoices().hashCode());
        result = prime * result + ((getResultId() == null) ? 0 : getResultId().hashCode());
        result = prime * result + ((getResultName() == null) ? 0 : getResultName().hashCode());
        result = prime * result + ((getResultDesc() == null) ? 0 : getResultDesc().hashCode());
        result = prime * result + ((getResultPicture() == null) ? 0 : getResultPicture().hashCode());
        result = prime * result + ((getResultScore() == null) ? 0 : getResultScore().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appId=").append(appId);
        sb.append(", appType=").append(appType);
        sb.append(", scoringStrategy=").append(scoringStrategy);
        sb.append(", choices=").append(choices);
        sb.append(", resultId=").append(resultId);
        sb.append(", resultName=").append(resultName);
        sb.append(", resultDesc=").append(resultDesc);
        sb.append(", resultPicture=").append(resultPicture);
        sb.append(", resultScore=").append(resultScore);
        sb.append(", userId=").append(userId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}