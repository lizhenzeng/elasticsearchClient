package com.tigerobo.example.entity;

import com.tiger.functool.base.BaseSuperDTO;
import lombok.Data;

@Data
public class Sample extends BaseSuperDTO {
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
