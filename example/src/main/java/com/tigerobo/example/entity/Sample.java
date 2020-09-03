package com.tigerobo.example.entity;

import com.tiger.functool.base.BaseSuperDTO;
import com.tigerobo.search.annotation.Table;
import lombok.Data;

import javax.persistence.Id;

@Data
@Table(index="mysteel_news_tag_v1",type = "news")
public class Sample extends BaseSuperDTO {
    @Id
    private String id;
    private String newsId;
    private String source;
    private String subjectNames;
    private String subjectIds;
    private String titleSegs;
    private String tagIds;
    private String riskIds;
    private String publishDateEnd;
    private String publishDateStart;
    private String titleEmotion;
    private String state;
    private String title;
}
