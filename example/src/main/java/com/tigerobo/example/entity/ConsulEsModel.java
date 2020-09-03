package com.tigerobo.example.entity;

import com.tiger.functool.base.BaseSuperDTO;
import com.tigerobo.search.annotation.Table;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

@Data
@Table(index = "mysteel_news_tag_v1", type = "news")
public class ConsulEsModel extends BaseSuperDTO {

    @Id
    private Long id;
    private String titleSegs;
    /**
     * 业务标签
     */
    private List<ConsulTagModel> tagIds;
    private String publishDate;
    private Long newsId;
    private List<Integer> tagIdStr;
    /**
     * 风险标签
     */
    private List<ConsulRiskModel> riskIds;
    private IgnoreOutputModel blackWords;
    private Double titleEmotion;
    /**
     * 资讯来源
     */
    private String source;
    private String title;
    private String contentSegs;

    /**
     * 资讯发布栏目
     */
    private String subject;
    /**
     * 敏感词列表 jsonString
     */
    private Integer isBlack;
    /**
     * 审核状态
     */
    private String state;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 更新时间
     */
    private String updateTime;

    @Transient
    private String content;
    @Transient
    private String publishDateStart;
    @Transient
    private String publishDateEnd;
    @Transient
    private String tagId;
    @Transient
    private Integer matchModel;
    @Transient
    private String riskId;
}
